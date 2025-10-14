package org.example

import com.android.tools.metalava.cli.common.ExecutionEnvironment
import java.io.File


fun main() {
    val jar = File("/Users/farhad/Projects/metalava/core-1.2.0.aar/classes.jar")
    val report = File("/Users/farhad/Projects/metalava/report.txt")
    val filteredReport = File("/Users/farhad/Projects/metalava/report-filtered.txt")

    // because metalava kills the process
    Runtime.getRuntime().addShutdownHook(Thread {
        println("🔍 Metalava done with ${report.name} ...")

        filterReport(input = report, output = filteredReport)

        println("✅ Filtered report written to: ${filteredReport.absolutePath}")
    })

    generateFullReport(input = jar, output = report)
}

fun generateFullReport(input: File, output: File) {
    println("🔍 Running Metalava on ${input.name} ...")
    runCatching {
        com.android.tools.metalava.main(
            arrayOf(
                "--source-files", input.absolutePath,
                "--api", output.absolutePath,
            )
        )
    }
}

fun filterReport(input: File, output: File) {
    val lines = input.readLines()
    val regex = Regex("tpsl", RegexOption.IGNORE_CASE)

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
