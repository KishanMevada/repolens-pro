plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt) // ✅ Hilt માટે
    alias(libs.plugins.ksp)  // ✅ KSP માટે
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.repolenspro.core.network"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // 🌐 Retrofit & OkHttp (API કૉલ માટે)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // 💉 Hilt (Dependency Injection માટે)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}