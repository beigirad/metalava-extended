plugins {
    kotlin("jvm") version "1.8.20"
    `kotlin-dsl`
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
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("metalava-extended-plugin") {
            id = "metalava-extended"
            implementationClass = "ir.beigirad.metalavaextended.TapsellCompatibilityPlugin"
        }
    }
}