import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class CommonModulePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply("kotlin-android")
        project.plugins.apply("kotlin-kapt")
        project.plugins.apply("kotlin-parcelize")

        val androidExtension = project.extensions.getByName("android")

        androidExtension.apply {
            if (androidExtension is BaseExtension) {
                androidExtension.apply {
                    compileSdkVersion(32)
                    buildToolsVersion("30.0.3")

                    defaultConfig {
                        if (project.name == "app") {
                            applicationId = "net.sharetrip"
                        }
                        targetSdk = 32
                        minSdk = 21

                        versionCode = 150
                        versionName = "3.0.0"

                        multiDexEnabled = true
                        renderscriptTargetApi = 30
                        renderscriptSupportModeEnabled = true
                        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }

                    dataBinding {
                        isEnabled = true
                    }

                    lintOptions {
                        isCheckReleaseBuilds = false
                    }

                    compileOptions {
                        sourceCompatibility = JavaVersion.VERSION_1_8
                        targetCompatibility = JavaVersion.VERSION_1_8
                    }

                    project.tasks.withType(KotlinCompile::class.java).configureEach {
                        kotlinOptions {
                            jvmTarget = "1.8"
                        }
                    }
                }
            }
        }

        project.dependencies {
            project.retrofit()
            project.core()
            project.coroutines()
            project.lifecycle()
            project.navigation()
            project.firebase()
            project.facebook()
            project.playServices()
            project.converter()
            project.storage()
            project.mixed()
            project.imageLibrary()
            project.testImplementation()
            project.annotationProcessor()
            project.workManager()
            project.crisp()
        }
    }
}
