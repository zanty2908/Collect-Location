plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "com.zanty.fossil.collectlocation"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("debug") {
            isDebuggable = true
        }
        named("release") {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

object Version {
    const val APPCOMPAT = "1.3.0"
    const val COROUTINES = "1.3.3"
    const val MAPS = "17.0.0"
    const val LIFECYCLE = "2.2.0"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:${Version.APPCOMPAT}")
    implementation("com.google.android.material:material:${Version.APPCOMPAT}")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime:${Version.LIFECYCLE}")
    implementation("androidx.lifecycle:lifecycle-extensions:${Version.LIFECYCLE}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Version.LIFECYCLE}")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.COROUTINES}")

    // Maps
    implementation("com.google.android.gms:play-services-location:${Version.MAPS}")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
