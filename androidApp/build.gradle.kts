plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(Libs.Lifecycle.livedata)
    implementation(Libs.Lifecycle.viewModel)

    implementation(Libs.Widgets.recyclerView)
    implementation(Libs.Widgets.cardView)
    implementation(Libs.Widgets.constraintLayout)

    implementation(Libs.Navigation.ui)
    implementation(Libs.Navigation.fragment)

    implementation(Libs.Koin.android)

    implementation(platform(Libs.Analytics.platform))
    implementation(Libs.Analytics.crashlytics)

    implementation(project(":shared"))
}

android {

    compileSdkVersion(App.Android.compileSDKVersion)
    defaultConfig {
        applicationId = App.ID
        minSdkVersion(App.Android.minSDKVersion)
        targetSdkVersion(App.Android.targetSDKVersion)
        versionCode = App.Version.code
        versionName = App.Version.name
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
repositories {
    mavenCentral()
}