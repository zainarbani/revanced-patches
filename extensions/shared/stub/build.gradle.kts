plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "app.revanced.extension"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }
}
