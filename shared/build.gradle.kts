import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")

    id("kotlinx-serialization")
    id("com.android.library")
    id("koin")
}

kotlin {
    jvm()
    android()
    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Libs.Koin.core)

                implementation(Libs.KtorClient.core)
                implementation(Libs.KtorClient.logging)
                implementation(Libs.KtorClient.serialization)
                implementation(Libs.KtorClient.json)
                implementation(Libs.KtorClient.websockets)

                // Kotlinx Serialization
                implementation(Libs.kotlinSerialization)

                implementation(Libs.Coroutines.core) {
                    isForce = true
                }
            }
        }
        val commonTest by getting
        val androidMain by getting{
            dependencies {
                implementation(Libs.Koin.android)
                implementation(Libs.KtorClient.android)
                implementation(Libs.KtorClient.okhttp)

            }
        }
        val androidTest by getting
        val iosMain by getting
        val iosTest by getting
    }
}

android {
    compileSdkVersion(App.Android.compileSDKVersion)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdkVersion(App.Android.minSDKVersion)
        targetSdkVersion(App.Android.targetSDKVersion)
        versionCode = App.Version.code
        versionName = App.Version.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

tasks.getByName("build").dependsOn(packForXcode)