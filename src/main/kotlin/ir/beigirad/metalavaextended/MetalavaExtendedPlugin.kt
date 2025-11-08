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
class MetalavaExtendedPlugin : Plugin<Project> {
    private val tasksGroup = "Metalava-Extended Compatibility Validator"

    private val generateTaskName = "generateCompatibilityReport"
    private val deleteTaskName = "deleteCompatibilityReport"

    override fun apply(project: Project): Unit = with(project) {
        val extension = extensions.create("metalavaExtended", MetalavaExtendedExtension::class.java)

        afterEvaluate {
            val aars = (buildDir / "outputs" / "aar").walkTopDown()
                .filter { it.isFile && (it.extension == "aar") }
            val jars = (buildDir / "libs").walkTopDown()
                .filter { it.isFile && (it.extension == "jar") }
            val allJars = aars.toList() + jars.toList()

            allJars.forEach { jarFile ->
                val reportPrefix = jarFile.nameWithoutExtension.removeSuffix("-release")

                val fullReportFile = if (extension.reportDir.isPresent)
                    extension.reportDir.asFile.get() / "$reportPrefix-full.api"
                else
                    buildDir / "compatibility-plugin" / "$reportPrefix-full.api"

                val filteredReportFile = if (extension.filteredReportDir.isPresent)
                    extension.filteredReportDir.asFile.get() / "$reportPrefix.api"
                else
                    projectDir / "$reportPrefix.api"

                tasks.register("$generateTaskName-${jarFile.name}", GenerateTask::class) {
                    this.jarFile.set(jarFile)
                    this.reportFile.set(fullReportFile)
                    this.filteredReportFile.set(filteredReportFile)
                    this.ignoreList.set(extension.ignoreList)
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
