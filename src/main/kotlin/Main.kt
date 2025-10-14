package ir.beigirad

import ir.beigirad.consolehelper.ArgProcessor
import java.io.File
import java.util.zip.ZipFile

fun main(rawArgs: Array<String>) {
    val args = ArgProcessor(rawArgs)
    val inputFile = File(args["jar-file"])
    val filter = args.getOrNull("filter")
    val reportFile = File(args["report"])
    val miniReportFile = args.getOrNull("mini-report")?.let { File(it) }

    val jarFile = prepareInputJar(inputFile)

    // because metalava kills the process
    Runtime.getRuntime().addShutdownHook(Thread {
        println("🔍 Metalava done with ${reportFile.name} ...")

        if (filter != null && miniReportFile != null)
            filterReport(input = reportFile, output = miniReportFile, filter = filter)

        println("✅ Report was written to: ${reportFile.absolutePath} and ${miniReportFile?.absolutePath}")

        if (jarFile != inputFile)
            File("classes-${inputFile.nameWithoutExtension}.jar").delete()
    })

    generateFullReport(input = jarFile, output = reportFile)
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

fun filterReport(input: File, output: File, filter: String) {
    val lines = input.readLines()
    val regex = Regex(filter, RegexOption.IGNORE_CASE)

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
