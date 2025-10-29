plugins {
    kotlin("jvm") version "1.8.20"
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "ir.beigirad"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(gradleApi())
    testImplementation(kotlin("test"))
}

gradlePlugin {
    website.set("https://github.com/beigirad/metalava-extended")
    vcsUrl.set("https://github.com/beigirad/metalava-extended")
    plugins {
        create("metalava-extended-plugin") {
            id = "metalava-extended"
            implementationClass = "ir.beigirad.metalavaextended.TapsellCompatibilityPlugin"
            displayName = "Metalava-Extended Gradle Plugin"
            description = "An extended version of metalava gradle plugin"
            tags.set(listOf("metalava", "api-compatibility"))
        }
    }
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