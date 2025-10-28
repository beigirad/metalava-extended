package ir.beigirad.metalavaextended

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * A Gradle plugin that generates a binary compatibility validation report for the public API
 * of an Android library project.
 *
 * This plugin uses the `metalava` tool to generate an API signature files.
 * In addition to default functionality of metalava, it provides filtering operations and support for aar files.
 *
 * Use below codes to run the generation or validation tasks:
 * ```bash
 * ./gradlew generateCompatibilityReport
 *
 * ./gradlew checkCompatibilityReport
 *
 * ./gradlew deleteCompatibilityReport
 * ```
 *
 * known issues:
 * - task `checkCompatibilityReport` doesn't work suit and detects some unrelated changes. It
 * should be fixed and only use it for compatibility verification.
 */
class TapsellCompatibilityPlugin : Plugin<Project> {
    private val tasksGroup = "Metalava-Extended Compatibility Validator"

    private val generateTaskName = "generateCompatibilityReport"
    private val deleteTaskName = "deleteCompatibilityReport"

    override fun apply(project: Project): Unit = with(project) {
        afterEvaluate {
            val allJars = (buildDir / "outputs" / "aar").walkTopDown()
                .filter { it.isFile && (it.extension == "aar") }
                .toList()
                .plus(
                    (buildDir / "libs").walkTopDown()
                    .filter { it.isFile && (it.extension == "jar") }
                    .toList()
                )

            allJars.forEach { jarFile ->
                val reportPrefix = jarFile.nameWithoutExtension.removeSuffix("-release")
                val fullReportFile =
                    buildDir / "compatibility-plugin" / "$reportPrefix.api-full.txt"
                val filteredReportFile = projectDir / "$reportPrefix.api.txt"

                tasks.register("$generateTaskName-${jarFile.name}", GenerateTask::class) {
                    this.jarFile.set(jarFile)
                    this.reportFile.set(fullReportFile)
                    this.filteredReportFile.set(filteredReportFile)
                    // to skip `tpsl` phrases that are produces by proguard!
                    // we assume all minified method are private api and changes of them
                    // is not important for end-users
                    this.ignore.set("tpsl")
                }

                tasks.register("$deleteTaskName-${jarFile.name}", Delete::class) {
                    delete(fullReportFile, filteredReportFile)
                }
            }

            tasks.register(generateTaskName) {
                group = tasksGroup
                description = "Generate compatibility report for public api"
                dependsOn(*allJars.map { "generateCompatibilityReport-${it.name}" }
                    .toTypedArray())
            }

            tasks.register("checkCompatibilityReport") {
                group = tasksGroup
                description = "Check compatibility report is valid or not"
                // dependsOn(variantsants.map { "metalavaCheckCompatibility$it" }.toTypedArray())
            }

            tasks.register(deleteTaskName) {
                group = tasksGroup
                description = "Delete current report files"
                dependsOn(*allJars.map { "$deleteTaskName-${it.name}" }.toTypedArray())
            }
        }
    }

}

operator fun File.div(next: String): File = File("${this.path}/$next")
