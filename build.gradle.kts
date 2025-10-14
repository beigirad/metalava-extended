plugins {
    kotlin("jvm") version "2.1.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.android.tools.metalava:metalava:1.0.0-alpha13")
}

tasks.test {
    useJUnitPlatform()
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