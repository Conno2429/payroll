package io.github.conno2429.util

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Workbook
import io.github.conno2429.Template.templateCols
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.rows

class Filler(wb: Workbook, private val keyName: String, private val firstRow: Int = 6) {
    private val sheet = wb.getSheetAt(0)
    private val rowOf = mutableMapOf<String, Int>()
    private var next = firstRow

    init {
        val keyCol = templateCols.getValue(keyName)
        val fmt = DataFormatter()
        for (r in firstRow..sheet.lastRowNum) {
            val key = fmt.formatCellValue(sheet.getRow(r)?.getCell(keyCol)).trim()
            if (key.isNotEmpty()) { rowOf[key] = r; next = r + 1 }
        }
    }

    fun populate(source: AnyFrame, createMissing: Boolean = true) {
        source.rows().forEach { row ->
            val key = row[keyName]?.toString()?.trim().orEmpty()
            if (key.isEmpty()) return@forEach

            val r = if (createMissing) rowOf.getOrPut(key) { next++ }
            else rowOf[key] ?: run { println("Skipped unmatched key: $key"); return@forEach }
            val sheetRow = sheet.getRow(r) ?: sheet.createRow(r)

            source.columnNames().forEach { name ->
                val c = templateCols[name] ?: return@forEach
                val cell = sheetRow.getCell(c) ?: sheetRow.createCell(c)
                when (val v = row[name]) {
                    null -> cell.setBlank()
                    is Number -> cell.setCellValue(v.toDouble())
                    else -> cell.setCellValue(v.toString())
                }
            }
        }
    }
}