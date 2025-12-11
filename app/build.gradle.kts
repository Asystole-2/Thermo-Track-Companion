plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}


android {
    namespace = "com.example.thermotrackcompanion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.thermotrackcompanion"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

//dependencies {
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.material3)
//    implementation(libs.androidx.room.common.jvm)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
//}

dependencies {
    // Standard Compose Dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Logging (Timber)
    implementation("com.jakewharton.timber:timber:5.0.1") //  Logging

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.5") //  Navigation

    // Architecture Components (ViewModel extensions)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Retrofit for Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0") //  Getting Data from Internet
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0") // Load and Display Images

    // Room for Database Persistence
    implementation("androidx.room:room-runtime:2.6.1") //  Room DB
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // DataStore for Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0") //  DataStore

    // Compose Animation
    implementation("androidx.compose.animation:animation:1.6.0") // Animation

    //splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Required for Thermostat, WaterDrop,
    implementation("androidx.compose.material:material-icons-extended")

    //adjusting window size
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0")

}