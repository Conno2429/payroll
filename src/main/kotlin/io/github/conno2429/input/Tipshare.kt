package io.github.conno2429.input

import io.github.conno2429.Template
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import java.io.File
import java.util.Date


object Tipshare {
    private const val KEY_COL = 0                                           // A
    private val TOTAL_COL = CellReference.convertColStringToIndex("AX")     // TOTAL
    private val ID = Regex("""^[A-Za-z]{2,}[A-Za-z0-9]*\d+(\s*-\s*v)?$""", RegexOption.IGNORE_CASE)
    private val VARIANT = Regex("""\s*-\s*v$""", RegexOption.IGNORE_CASE)

    fun load(path: String): AnyFrame {
        val f = File(path)
        println("tipshare: exists=${f.exists()} size=${f.length()} modified=${Date(f.lastModified())}")

        return WorkbookFactory.create(f, null, true).use { wb ->
            val sheet = wb.getSheet("Tip Pool") ?: error("No 'Tip Pool' sheet. Have: ${(0 until wb.numberOfSheets).map { wb.getSheetName(it) }}")
            val fmt = DataFormatter()

            fun text(c: Cell?) = fmt.formatCellValue(c).replace('\u00A0', ' ').trim()
            fun num(c: Cell?): Double? = when (c?.cellType) {
                CellType.NUMERIC -> c.numericCellValue
                CellType.FORMULA -> if (c.cachedFormulaResultType == CellType.NUMERIC) c.numericCellValue else null
                CellType.STRING -> c.stringCellValue.replace(Regex("[$,\\s]"), "").toDoubleOrNull()
                else -> null
            }

            val hits = (0..sheet.lastRowNum).mapNotNull { r ->
                val row = sheet.getRow(r) ?: return@mapNotNull null
                val id = text(row.getCell(KEY_COL))
                val tip = num(row.getCell(TOTAL_COL))
                if (ID.matches(id) && tip != null && tip != 0.0) Triple(r + 1, id.replace(VARIANT, ""), tip) else null
            }
            println("tipshare: ${hits.size} rows with ID + nonzero TOTAL, sheet rows ${hits.minOfOrNull { it.first }}..${hits.maxOfOrNull { it.first }}")

            // IDs that appear more than once (a second block repeating people, or -v variants)
            hits.groupBy { it.second }.filter { it.value.size > 1 }
                .forEach { (id, v) -> println("  repeated: $id on rows ${v.map { it.first }} amounts ${v.map { it.third }}") }

            val totals = hits.groupBy({ it.second }, { it.third })
                .mapValues { Math.round(it.value.sum() * 100) / 100.0 }

            dataFrameOf(
                totals.keys.toList().toColumn(Template.KEY),
                totals.values.toList().toColumn("Tips"),
            )
        }
    }

    val preparedTipshare = load("C:/Users/conno/Downloads/tipshare_test.xlsx")
}