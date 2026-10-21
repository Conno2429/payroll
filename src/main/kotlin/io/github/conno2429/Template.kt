package io.github.conno2429

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory

object Template {
    const val KEY = "Employee number"
    const val HEADER_ROW = 5        // 0-based, sheet row 6
    const val FIRST_DATA_ROW = 6    // sheet row 7
    const val LAST_DATA_ROW = 65    // sheet row 66; row 67 holds the totals

    val url = object {}.javaClass.getResource("/payroll-template.xlsx")
        ?: error("payroll-template.xlsx not found")

    // header text -> real sheet column index; first occurrence wins
    // (hours columns E-G come before the pay columns M-O)
    val templateCols: Map<String, Int> = url.openStream().use { stream ->
        WorkbookFactory.create(stream).use { wb ->
            val fmt = DataFormatter()
            val header = wb.getSheetAt(0).getRow(HEADER_ROW)
            val map = linkedMapOf<String, Int>()
            for (c in 0 until header.lastCellNum) {
                val name = fmt.formatCellValue(header.getCell(c)).trim()
                if (name.isNotEmpty()) map.putIfAbsent(name, c)
            }
            map
        }
    }
}