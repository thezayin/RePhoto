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
        jcenter()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url= uri("https://developer.huawei.com/repo/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url= uri("https://developer.huawei.com/repo/") }
    }
}

rootProject.name = "RePhoto"
include(":app")
include(":start-up")
include(":gallery:data")
include(":common:framework")
include(":common:components")
include(":common:analytics")
include(":common:values")
include(":gallery:domain")
include(":gallery:presentation")
include(":feature:background")
include(":feature:background-remover")
include(":feature:background-changer")
include(":feature:enhance")
include(":feature:background-blur")
include(":feature:editor")
