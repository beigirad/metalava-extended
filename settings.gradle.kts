rootProject.name = "metalava-extended"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
pluginManagement {
    includeBuild("plugin")
}

include("sample")

