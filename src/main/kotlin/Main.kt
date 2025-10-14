package org.example

import java.io.File

fun main() {
    val jar = File("/Users/farhad/Projects/metalava/core-1.2.0.aar/classes.jar")
    val report = File("/Users/farhad/Projects/metalava/report.txt")
    val filteredReport = File("/Users/farhad/Projects/metalava/report-filtered.txt")

    generateFullReport(
        input = jar,
        output = report
    )

    filterReport(
        input = report,
        output = filteredReport
    )

    println("✅ Filtered report written.")
}

fun generateFullReport(input: File, output: File) {
    com.android.tools.metalava.main(
        arrayOf(
            "--source-files", input.absolutePath,
            "--api", output.absolutePath,
        )
    )
}

fun filterReport(input: File, output: File) {
    val lines = input.readLines()
    val regex = Regex("tpsl")

    val result = mutableListOf<String>()
    var skipBlock = false
    var braceDepth = 0

    for (line in lines) {
        if (!skipBlock && regex.containsMatchIn(line)) {
            skipBlock = true
        }

        if (skipBlock) {
            braceDepth += line.count { it == '{' }
            braceDepth -= line.count { it == '}' }
            if (braceDepth <= 0) skipBlock = false
        } else {
            result += line
        }
    }

    output.writeText(result.joinToString("\n"))
}