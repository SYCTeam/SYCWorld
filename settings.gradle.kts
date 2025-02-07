pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven(url = "https://maven.aliyun.com/repository/public/")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        //maven(url = "https://maven.aliyun.com/repository/public/")
        google()
        mavenCentral()
    }
}

rootProject.name = "SYCWorld"
include(":app")
