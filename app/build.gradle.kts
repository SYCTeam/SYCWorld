import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

fun getAndIncrementBuildNumber(): Int {
    val propertiesFile = file("version.properties")
    val properties = Properties()
    // Load existing properties
    if (propertiesFile.exists()) {
        properties.load(FileInputStream(propertiesFile))
    } else {
        // If file doesn't exist, create a new one
        properties["BUILD_NUMBER"] = "1"
    }
    // Get the current build number
    val buildNumber = properties["BUILD_NUMBER"].toString().toInt()
    // Increment the build number
    properties["BUILD_NUMBER"] = (buildNumber + 1).toString()
    // Save the updated build number back to the properties file
    properties.store(FileOutputStream(propertiesFile), null)
    return buildNumber
}

fun commitCount(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = "git rev-list --count HEAD".split(" ")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun getGitCommitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = "git rev-parse --short HEAD".split(" ")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

android {
    namespace = "com.syc.world"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.syc.world"
        minSdk = 30
        targetSdk = 35
        versionCode = commitCount().toInt()
        versionName = "1.0"+".b"+commitCount().toInt()+"."+getGitCommitHash()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            //proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.glide)
    implementation(libs.image.viewer)
    implementation("com.jvziyaoyao.scale:sampling-decoder:1.1.0-alpha.5")
    implementation(libs.multiplatform.markdown.renderer.code)
    implementation(libs.multiplatform.markdown.renderer.coil3)
    implementation(libs.multiplatform.markdown.renderer.android)
    implementation(libs.play.services.fitness)
    implementation(libs.play.services.location)
    implementation(libs.gson)
    implementation(libs.lottie.compose)
    implementation(libs.okhttp)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.miuix)
    implementation(libs.haze)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
