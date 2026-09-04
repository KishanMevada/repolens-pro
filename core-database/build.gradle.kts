plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt) // ✅ Hilt માટે
    alias(libs.plugins.ksp)  // ✅ KSP માટે
}

android {
    namespace = "com.repolenspro.core.database"
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

    // 🗄️ Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Paging 3 (જો ડેટાબેઝમાં વાપરતા હોવ તો)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.runtime)

    // 💉 Hilt (Dependency Injection માટે)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}