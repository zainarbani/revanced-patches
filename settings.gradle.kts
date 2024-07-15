rootProject.name = "revanced-patches"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        // TODO: Remove, this is only for plugin testing
        mavenLocal()
    }
}

// TODO: Remove, this is only for local patcher v20
@Suppress("UnstableApiUsage")
dependencyResolutionManagement.apply {
    repositories.apply {
        mavenLocal()
    }
}

plugins {
    id("app.revanced.patches.settings.plugin") version "1.0.0"
}
