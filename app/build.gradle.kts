import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.aula.marvel"
    compileSdk { version = release(36) }

    defaultConfig {
        applicationId = "com.aula.marvel"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // api_key da Comic Vine: local.properties (CV_API_KEY=...) ou gradle.properties — nunca commitada
        val localProps = Properties().apply {
            val f = rootProject.file("local.properties")
            if (f.exists()) f.inputStream().use { load(it) }
        }
        val cvKey = (localProps.getProperty("CV_API_KEY")
            ?: project.findProperty("CV_API_KEY") as String?
            ?: "")
        manifestPlaceholders["CV_API_KEY"] = cvKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures { viewBinding = true }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.mlkit.translate)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
