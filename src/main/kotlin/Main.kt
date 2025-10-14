package ir.beigirad

import java.io.File
import java.util.zip.ZipFile


fun main() {
    val file = File("/Users/farhad/Projects/metalava/core-release.aar")
    val jar = prepareInputJar(file)
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

fun prepareInputJar(input: File): File =
    if (input.extension == "aar") {
        val extracted = File(input.parentFile, "classes-${input.nameWithoutExtension}.jar")
        println("📦 Extracting classes.jar from ${input.name} ...")

        ZipFile(input).use { zip ->
            val entry = zip.getEntry("classes.jar")
                ?: error("No classes.jar found inside ${input.name}")
            zip.getInputStream(entry).use { inputStream ->
                extracted.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        println("✅ Extracted to ${extracted.absolutePath}")
        extracted
    } else {
        input
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
