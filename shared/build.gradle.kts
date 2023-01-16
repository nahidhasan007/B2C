plugins {
    id("com.android.library")
    id("common-module-plugin")
}

android {
    dataBinding {
        isEnabled = true
    }
}

dependencies {
    implementation(project(":base"))
}