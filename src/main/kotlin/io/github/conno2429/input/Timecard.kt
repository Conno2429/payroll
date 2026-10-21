package io.github.conno2429.input

import io.github.conno2429.Template
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readExcel

object Timecard {
    val timecard = DataFrame.readExcel("C:/Users/conno/Downloads/timecard_test.xlsx/", firstRowIsHeader = false)

    val timecardRenames = mapOf(
        "Doubletime hours" to "2.0x",
        "Regular hours" to "Reg Hrs",
        "Overtime hours" to "1.5x",
    )

    fun prepareTimecard(raw: AnyFrame): AnyFrame {
        val headerIdx = (0 until raw.rowsCount()).firstOrNull { i ->
            raw[i].values().any { it?.toString()?.trim() == Template.KEY }
        } ?: error("Header row containing '${Template.KEY}' not found in timecard")
        val firstSix = raw.columnNames().take(6)
        val headerRow = raw[headerIdx]

        val names = firstSix.map { col ->
            val h = headerRow[col]?.toString()?.trim().orEmpty()
            timecardRenames[h] ?: h
        }

        var df: AnyFrame = raw.select(*firstSix.toTypedArray())
            .drop(headerIdx + 1)
            .rename { all() }.to(*names.toTypedArray())

        val idName = Template.KEY
        df = df.convert(idName).with { v: Any? ->
            if (v is Number) v.toLong().toString() else v?.toString()?.trim().orEmpty()
        }

        df = df.convert(*listOf("Reg Hrs", "1.5x", "2.0x").toTypedArray()).with { v: Any? ->
            (v as? Number)?.toDouble() ?: v?.toString()?.trim()?.toDoubleOrNull() ?: 0.0
        }

        val keep = (0 until df.rowsCount()).filter { i -> df[i][idName].toString().isNotBlank() }
        return df[keep]
    }

    val timecardNamed = prepareTimecard(timecard)
}