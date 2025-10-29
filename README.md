[![](https://jitpack.io/v/beigirad/metalava-extended.svg)](https://jitpack.io/#beigirad/metalava-extended)

# 🚧 Metalava-Extended Gradle Plugin (WIP) 🚧

An extended version of [metalava](https://android.googlesource.com/platform/tools/metalava) gradle plugin:

Features:
- Support inspecting `aar` files
- Support filtering the output report
- ...


### Usage

Define the dependency in root `build.gradle`
```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    
    dependencies {
        classpath 'com.github.beigirad.metalava-extended:metalava-extended:VERSION'
    }
}
```

Apply the plugin in each module's `build.gradle`:

```groovy
apply plugin: 'metalava-extended'
```