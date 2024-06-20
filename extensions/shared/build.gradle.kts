plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

ext["extensionResourceName"] = "extensions/shared.rve"

android {
    namespace = "app.revanced.extension"
    compileSdk = 33

    defaultConfig {
        applicationId = "app.revanced.extension"
        minSdk = 23
        multiDexEnabled = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    compileOnly(libs.appcompat)
    compileOnly(libs.annotation)
    compileOnly(libs.okhttp)
    compileOnly(libs.retrofit)

    compileOnly(project(":extensions:shared:stub"))
}
