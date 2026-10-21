package io.github.conno2429.util

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import io.github.conno2429.Template
import java.io.File

fun export(wb: Workbook, out: File) {
    wb.creationHelper.createFormulaEvaluator().evaluateAll()
    out.outputStream().use { wb.write(it) }
}

fun loadTemplate(): Workbook =
    Template.url.openStream().use { WorkbookFactory.create(it) }

fun norm(v: Any?): String =
    v?.toString().orEmpty().replace(Regex("\\s+"), " ").trim().lowercase()

fun uniqueFile(file: File): File {
    if (!file.exists()) return file

    val base = file.nameWithoutExtension
    val ext = file.extension.let { if (it.isEmpty()) "" else ".$it" }

    var n = 1
    var candidate: File
    do {
        candidate = File(file.parentFile, "$base ($n)$ext")   // or "$base-$n$ext"
        n++
    } while (candidate.exists())
    return candidate
}