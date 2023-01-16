plugins {
    id("com.android.library")
    id("common-module-plugin")
    id("kotlin-android")
}

android {
    buildTypes {
        release {
            manifestPlaceholders["enableCrashReporting"] = "true"
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
        debug {
            manifestPlaceholders["enableCrashReporting"] = "false"
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":base"))
    implementation(project(":shared"))
    implementation(project(":payment"))
}
