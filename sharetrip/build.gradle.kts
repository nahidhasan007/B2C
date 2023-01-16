plugins {
    id("com.android.application")
    id("common-module-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("org.jetbrains.kotlin.android")
}

android {
    buildTypes {
        release {
            manifestPlaceholders["enableCrashReporting"] = "true"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
            isCrunchPngs = true

        }
        debug {
            manifestPlaceholders["enableCrashReporting"] = "false"
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
            isCrunchPngs = false
        }
    }
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }

}

dependencies {
    implementation(project(":base"))
    implementation(project(":shared"))
//  implementation(project(":bus"))
    implementation(project(":flight"))
    implementation(project(":holiday"))
    implementation(project(":hotel"))
    implementation(project(":visa"))
    //implementation project(path: ':tour')
    implementation(project(":signup"))
    implementation(project(":wheel"))
    implementation(project(":tracker"))
    implementation(project(":profile"))
    implementation("io.socket:socket.io-client:1.0.0") {
        exclude(
            group = "org.json",
            module = "json"
        )
    }
}
