extension {
    name = "extensions/shared.rve"
}

android {
    namespace = "app.revanced.extension"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    compileOnly(libs.appcompat)
    compileOnly(libs.annotation)
    compileOnly(libs.okhttp)
    compileOnly(libs.retrofit)

    compileOnly(project(":extensions:shared:stub"))
}
