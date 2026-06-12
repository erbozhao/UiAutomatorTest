import com.android.build.gradle.api.ApkVariantOutput

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = 36
    namespace = "com.bbtest"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.bbtest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testNamespace = "com.bbtest.test"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../bbtest-keystore.jks")
            storePassword = "123456"
            keyAlias = "bbtest"
            keyPassword = "123456"
        }

        create("release") {
            storeFile = file("../bbtest-keystore.jks")
            storePassword = "123456"
            keyAlias = "bbtest"
            keyPassword = "123456"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
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

android {
    applicationVariants.configureEach {
        outputs.configureEach {
            val variantOutput = this as ApkVariantOutput
            variantOutput.outputFileName = when (buildType.name) {
                "debug" -> "bbtest.apk"
                "release" -> "bbtest_obfs.apk"
                else -> variantOutput.outputFileName
            }
        }
    }
}
dependencies {
    implementation(libs.guava)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.hamcrest.integration)
    androidTestImplementation(libs.hamcrest.library)
    androidTestImplementation(libs.espresso.core)

    testImplementation(libs.junit4)

    implementation(libs.commons.io)
}
