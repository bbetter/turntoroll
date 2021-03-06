object App {
    const val ID = "com.owlsoft.timetoroll"

    object Version {
        const val name = "1.0"
        const val code = 1
    }

    object Android {
        const val minSDKVersion = 21
        const val compileSDKVersion = 29
        const val targetSDKVersion = compileSDKVersion
    }
}

object Versions {
    object Plugins {
        const val android = "4.1.3"
        const val kotlin = "1.4.32"
    }

    object Libs {

        const val coroutines = "1.4.3-native-mt"
        const val lifecycle = "2.2.0"
        const val kotlinxSerialization = "1.0.1"
        const val koin = "3.0.1"
        const val navigation = "2.3.2"
        const val recyclerView = "1.1.0"
        const val cardView = "1.0.0"
        const val constraintLayout = "2.0.4"

        const val ktor = "1.5.3"
    }
}

object Plugins {
    const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.Plugins.kotlin}"
    const val gradle = "com.android.tools.build:gradle:${Versions.Plugins.android}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlin}"
    const val googleServices = "com.google.gms:google-services:4.3.4"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.4.1"
}

object Libs {
    const val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.Libs.kotlinxSerialization}"
    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Libs.coroutines}"
    }

    object Lifecycle {
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Libs.lifecycle}"
        const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.Libs.lifecycle}"
    }

    object Koin {
        private const val version = Versions.Libs.koin

        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
    }

    object Navigation {
        private const val version = Versions.Libs.navigation
        const val ui = "androidx.navigation:navigation-ui-ktx:$version"
        const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
    }

    object Widgets {
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.Libs.recyclerView}"
        const val cardView = "androidx.cardview:cardview:${Versions.Libs.cardView}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.Libs.constraintLayout}"
    }

    object KtorServer {
        const val test = "io.ktor:ktor-server-test-host:${Versions.Libs.ktor}"

        const val core = "io.ktor:ktor-server-core:${Versions.Libs.ktor}"
        const val netty = "io.ktor:ktor-server-netty:${Versions.Libs.ktor}"
        const val websockets = "io.ktor:ktor-websockets:${Versions.Libs.ktor}"
        const val serialization = "io.ktor:ktor-serialization:${Versions.Libs.ktor}"
    }

    object KtorClient {
        val core = "io.ktor:ktor-client-core:${Versions.Libs.ktor}"
        val json = "io.ktor:ktor-client-json:${Versions.Libs.ktor}"
        val logging = "io.ktor:ktor-client-logging:${Versions.Libs.ktor}"
        val serialization = "io.ktor:ktor-client-serialization:${Versions.Libs.ktor}"
        val websockets = "io.ktor:ktor-client-websockets:${Versions.Libs.ktor}"
        val android = "io.ktor:ktor-client-android:${Versions.Libs.ktor}"
        val ios = "io.ktor:ktor-client-ios:${Versions.Libs.ktor}"
        val okhttp = "io.ktor:ktor-client-okhttp:${Versions.Libs.ktor}"
    }

    object Analytics {
        val platform = "com.google.firebase:firebase-bom:26.3.0"
        val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    }
}

