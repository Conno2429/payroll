package io.github.conno2429

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import io.github.conno2429.input.Tipshare
import io.github.conno2429.input.Timecard
import io.github.conno2429.util.Filler
import io.github.conno2429.util.export
import io.github.conno2429.util.loadTemplate
import io.github.conno2429.util.uniqueFile
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellReference
import org.jetbrains.kotlinx.dataframe.api.take
import java.io.File

fun main() {
    val wb = loadTemplate()

    val filler = Filler(wb, keyName = Template.KEY)

    filler.populate(Timecard.timecardNamed)

    val path = "C:/Users/conno/Downloads/tipshare_test.xlsx"
    val f = File(path)
    println("exists=${f.exists()} size=${f.length()} modified=${java.util.Date(f.lastModified())}")

    WorkbookFactory.create(f, null, true).use { wb ->
        val fmt = DataFormatter()
        val s = wb.getSheetAt(0)
        println("sheet='${s.sheetName}' firstRow=${s.firstRowNum} lastRow=${s.lastRowNum}")
        for (r in s.firstRowNum..minOf(s.firstRowNum + 9, s.lastRowNum)) {
            val row = s.getRow(r) ?: continue
            val cells = row.filter { fmt.formatCellValue(it).isNotBlank() }
                .take(14)
                .map { "${CellReference.convertNumToColString(it.columnIndex)}='${fmt.formatCellValue(it).replace("\n", "\\n")}'" }
            println("row ${r + 1}: $cells")
        }
    }

    filler.populate(Tipshare.preparedTipshare, false)

    println(Tipshare.preparedTipshare.rowsCount())
    println(Tipshare.preparedTipshare.take(5))

    val out = uniqueFile(File(System.getProperty("user.home"), "Desktop/payroll-filled.xlsx"))
    export(wb, out)
    println("Saved to ${out.absolutePath}")

    wb.close()
}