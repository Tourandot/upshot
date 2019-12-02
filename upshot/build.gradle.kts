plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib")) // version is set by the plugin
    implementation("com.squareup.retrofit2:retrofit:${extra["retrofitVersion"]}")
    implementation("com.squareup.okhttp3:okhttp:${extra["okhttpVersion"]}")

    testImplementation("junit:junit:4.12")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.2.1")
    testImplementation("com.google.truth:truth:1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
}
