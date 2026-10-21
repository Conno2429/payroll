plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.dataframe") version "2.4.21"
}

group = "io.github.conno2429"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:dataframe:1.0.0-rc02")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}