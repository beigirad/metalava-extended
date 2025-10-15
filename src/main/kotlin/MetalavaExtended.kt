@file:JvmName("MetalavaExtended")

package ir.beigirad

import ir.beigirad.consolehelper.ArgProcessor
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.util.zip.ZipFile

fun main(rawArgs: Array<String>) {
    val args = ArgProcessor(rawArgs)
    val inputFile = File(args["compiled-file"])
    val filter = args.getOrNull("filter")
    val fullReportFile = File(args["report"])
    val filteredReportFile = args.getOrNull("filtered-report")?.let { File(it) }

    val jarFile = prepareInputJar(inputFile)

    // because metalava kills the process
    Runtime.getRuntime().addShutdownHook(Thread {
        println("🔍 Metalava done with ${fullReportFile.name} ...")

        if (filter != null && filteredReportFile != null)
            filteredReportFile.writeText(fullReportFile.readText(Charsets.UTF_8).filterReport(filter))

        println("✅ Report was written to: ${fullReportFile.absolutePath} and ${filteredReportFile?.absolutePath}")

        if (jarFile.path != inputFile.path)
            File("classes-${inputFile.nameWithoutExtension}.jar").delete()
    })

    generateFullReport(input = jarFile, output = fullReportFile)
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
                "--format=v4",
            )
        )
    }
}

@TestOnly
fun String.filterReport(filter: String): String {
    val lines = this.lines()
    val regex = Regex(filter, RegexOption.IGNORE_CASE)

    val result = mutableListOf<String>()
    var skipBlock = false

    for (line in lines) {

        if (line.isBlank()) continue

        if (skipBlock && line.contains("}")) {
            skipBlock = false
            continue
        }

        if (skipBlock) continue

        if (regex.containsMatchIn(line) && line.contains("{")) {
            skipBlock = true
            continue
        }

        if (regex.containsMatchIn(line)) continue

        result += line
    }

    return result.joinToString("\n")
}
