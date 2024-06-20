plugins {
    // Patches
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.binary.compatibility.validator) apply false
    // Extensions
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
}
