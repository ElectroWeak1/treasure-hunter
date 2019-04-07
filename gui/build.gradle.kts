import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    application
}

group = "sk.stuba.fiit.ui"
version = "0.0.1"

application {
    mainClassName = "sk.stuba.fiit.ui.treasure.gui.AppKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":evolution-algo"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.0-alpha")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.2.0-alpha")
    testImplementation("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}