plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.jummania.zoomableimageview"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jummania.zoomableimageview"
        minSdk = 16
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(libs.material)
    implementation(project(":Zoomable-ImageView"))
}