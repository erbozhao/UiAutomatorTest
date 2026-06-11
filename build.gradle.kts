buildscript {
    repositories {
        maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
        maven(url = uri("https://maven.aliyun.com/repository/central"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://maven.aliyun.com/repository/google"))
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}

extra.apply {
    set("androidxAnnotationVersion", "1.10.0")
    set("guavaVersion", "33.6.0-android")
    set("coreVersion", "1.7.0")
    set("extJUnitVersion", "1.3.0")
    set("runnerVersion", "1.7.0")
    set("rulesVersion", "1.7.0")
    set("espressoVersion", "3.7.0")
    set("uiAutomatorVersion", "2.4.0-beta02")
}
