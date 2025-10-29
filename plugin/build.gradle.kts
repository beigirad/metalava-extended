plugins {
    kotlin("jvm") version "1.8.20"
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "io.github.beigirad.metalava-extended"
version = "1.0"

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
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    website.set("https://github.com/beigirad/metalava-extended")
    vcsUrl.set("https://github.com/beigirad/metalava-extended")
    plugins {
        create("metalava-extended-plugin") {
            id = "io.github.beigirad.metalava-extended"
            implementationClass = "ir.beigirad.metalavaextended.TapsellCompatibilityPlugin"
            displayName = "Metalava-Extended Gradle Plugin"
            description = "An extended version of metalava gradle plugin"
            tags.set(listOf("metalava", "api-compatibility"))
        }
    }
}