plugins {
    kotlin("jvm") version "2.1.21"
    id("com.gradleup.shadow") version "9.1.0"
    application
}

group = "ir.beigirad"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") }
}

application {
    // Replace with your package and main class name
    mainClass.set("ir.beigirad.MainKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("com.android.tools.metalava:metalava:1.0.0-alpha06")
    implementation("com.github.beigirad:console-helper:0f927115c8")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    isZip64 = true
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