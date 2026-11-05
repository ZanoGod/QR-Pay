plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.0.21"
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
}

android {
    namespace = "com.example.qrpay"

    defaultConfig {
        applicationId = "com.example.qrpay"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk{
               abiFilters += ("armeabi-v7a") //for older device (to support older 32-bit devices,)
                abiFilters += ("arm64-v8a") //for most modern device (x64)

//                abiFilters += ("x86")
//                abiFilters += ("x86_64")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    buildToolsVersion = "34.0.0"
    compileSdk = 34


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material:material-icons-core:1.7.6")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")
    implementation("androidx.navigation:navigation-compose:2.8.5")

    implementation(libs.androidx.core.splashscreen)
    implementation("com.airbnb.android:lottie:6.6.2")
    implementation("androidx.compose.foundation:foundation:1.7.6")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    implementation(libs.androidx.datastore.core.android)

    //gson_converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // KSP dependencies
    implementation("com.google.dagger:dagger:2.54")

    //hilt
    implementation("com.google.dagger:hilt-android:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.androidx.runtime.livedata)
    kapt("com.google.dagger:hilt-android-compiler:2.54")

    //ksp(libs.moshi.kotlin.codegen)
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")

    //livedata
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    //lottie
    implementation("com.airbnb.android:lottie-compose:6.6.0")

    //swipe refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}