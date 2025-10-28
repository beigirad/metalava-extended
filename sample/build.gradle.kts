plugins {
    id("java")
    id("metalava-extended")
}

tasks.register("runMetalava", JavaExec::class.java) {
    mainClass.set("com.android.tools.metalava.Driver")
    classpath = sourceSets.main.get().runtimeClasspath
    val jarPath = "/Users/farhad/Projects/metalava/core-1.2.0.aar/classes.jar"
    val reportPath = "/Users/farhad/Projects/metalava/report.txt"
    args = listOf(
        "--source-files", jarPath,
        "--api", reportPath,
    )
}