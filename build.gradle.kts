plugins {
    `build-scan`
    kotlin("jvm") version "1.3.60"
}
apply(from = rootProject.file("maven-publish.gradle"))

allprojects {
    repositories {
        mavenCentral()
    }
    val retrofitVersion by extra("2.6.2")
    val okhttpVersion by extra("4.2.1")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}