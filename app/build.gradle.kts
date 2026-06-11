import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val guavaVersion: String by rootProject.extra
val androidxAnnotationVersion: String by rootProject.extra
val coreVersion: String by rootProject.extra
val extJUnitVersion: String by rootProject.extra
val runnerVersion: String by rootProject.extra
val rulesVersion: String by rootProject.extra
val espressoVersion: String by rootProject.extra
val uiAutomatorVersion: String by rootProject.extra

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

    applicationVariants.configureEach {
        outputs.configureEach {
            val variantOutput = this as BaseVariantOutputImpl
            variantOutput.outputFileName = when (buildType.name) {
                "debug" -> "bbtest.apk"
                "release" -> "bbtest_obfs.apk"
                else -> variantOutput.outputFileName
            }
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

dependencies {
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("androidx.annotation:annotation:$androidxAnnotationVersion")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-ktx:1.13.1")

    androidTestImplementation("androidx.test:core:$coreVersion")
    androidTestImplementation("androidx.test.ext:junit:$extJUnitVersion")
    androidTestImplementation("androidx.test:runner:$runnerVersion")
    androidTestImplementation("androidx.test:rules:$rulesVersion")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:$uiAutomatorVersion")
    androidTestImplementation("org.hamcrest:hamcrest-integration:1.3")
    androidTestImplementation("org.hamcrest:hamcrest-library:1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")

    testImplementation("junit:junit:4.13.2")

    implementation("commons-io:commons-io:2.22.0")
}
