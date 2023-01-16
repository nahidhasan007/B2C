plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("common-module-plugin") {
            id = "common-module-plugin"
            implementationClass = "CommonModulePlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())

    implementation("com.android.tools.build:gradle:7.0.3")
    implementation(kotlin("gradle-plugin", "1.6.21"))
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    implementation("com.google.gms:google-services:4.3.10")
    implementation("com.google.firebase:perf-plugin:1.4.0")
}
