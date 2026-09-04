plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // Flow (Coroutines) માટે
    implementation(libs.kotlinx.coroutines.core)

    // PagingData (Paging 3 - Pure Kotlin version) માટે
    implementation(libs.androidx.paging.common)
}
