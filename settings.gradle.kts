pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Calayo"
include(":app")

// --- FLUTTER INTEGRATION ---
// The flutter_app is a full application, so we include its inner android project.
include(":flutter")
project(":flutter").projectDir = file("flutter_app/android")
// --- END FLUTTER SECTION ---
