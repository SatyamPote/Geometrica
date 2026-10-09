plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nebulastrike.game"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nebulastrike.game"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0-nebula"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../candycrush.keystore")
            storePassword = "candy123"
            keyAlias = "candy"
            keyPassword = "candy123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // debug builds use the default debug key
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
