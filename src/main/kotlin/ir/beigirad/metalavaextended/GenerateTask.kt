package ir.beigirad.metalavaextended

import com.github.javaparser.metamodel.OptionalProperty
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.util.zip.ZipFile
import javax.inject.Inject
import kotlin.io.writeText

abstract class GenerateTask @Inject constructor(
    private val execOps: ExecOperations,
) : DefaultTask() {

    @get:InputFile
    abstract val jarFile: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val ignore: Property<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @get:OutputFile
    @get:Optional
    abstract val filteredReportFile: RegularFileProperty

    @TaskAction
    fun run() {
        val inputFile = prepareInputJar(jarFile.get().asFile)

        println("🔍 Running Metalava on ${inputFile.absolutePath} ...")
        execOps.javaexec {
            mainClass.set("com.android.tools.metalava.Driver")
            classpath = project.metalavaClasspath()
            args = listOf(
                "--source-files", inputFile.absolutePath,
                "--api", reportFile.get().asFile.absolutePath,
                "--format=v4",
            )
        }

        println("🔍 Metalava done with ${reportFile.get().asFile.name} ...")

        if (ignore.orNull != null && filteredReportFile.orNull != null)
            filteredReportFile.get().asFile.writeText(
                reportFile.get().asFile.readText(Charsets.UTF_8).filterReport(ignore.get())
            )

        println("✅ Report was written to: ${reportFile.get().asFile.absolutePath} and ${filteredReportFile.orNull?.asFile?.absolutePath}")

        if (jarFile.orNull?.asFile?.path != inputFile.path)
            inputFile.delete()
    }

    private fun prepareInputJar(input: File): File =
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


    private fun Project.metalavaClasspath(): FileCollection {
        val configuration = configurations.findByName("metalava-conf")
            ?: configurations.create("metalava-conf").apply {
                val dependency = this@metalavaClasspath.dependencies.create(
                    "com.android.tools.metalava:metalava:1.0.0-alpha06"
                )
                dependencies.add(dependency)
            }
        return files(configuration)
    }
}

@TestOnly
fun String.filterReport(ignore: String): String {
    val lines = this.lines()
    val regex = Regex(ignore, RegexOption.IGNORE_CASE)

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

