plugins {
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

android {
    namespace = "com.jummania"
    compileSdk = 34

    defaultConfig {
        minSdk = 16

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("ReleaseAar") {
                groupId = "com.github.Jumman04"
                artifactId = "Zoomable-ImageView"
                version = "1.1"
                afterEvaluate {
                    artifact(tasks.getByName("bundleReleaseAar"))
                }
            }
        }
    }
}