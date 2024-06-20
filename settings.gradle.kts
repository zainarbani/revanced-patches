rootProject.name = "revanced-patches"

buildCache {
    local {
        isEnabled = "CI" !in System.getenv()
    }
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        mavenLocal()
        google()
        maven {
            // A repository must be specified for some reason. "registry" is a dummy.
            url = uri("https://maven.pkg.github.com/revanced/registry")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

include(":patches")

file("extensions").listFiles()?.forEach { extension ->
    include(":extensions:${extension.name}")
    extension.listFiles()?.forEach { subExtension ->
        if (subExtension.resolve("build.gradle.kts").exists()) {
            include(":extensions:${extension.name}:${subExtension.name}")
        }
    }
}
