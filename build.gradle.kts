plugins {
    kotlin("jvm")
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