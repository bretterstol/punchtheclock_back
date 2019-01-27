import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.11"
}

group = "ButtonBack"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.javalin:javalin:2.5.0")
    compile("org.slf4j:slf4j-simple:1.7.25")
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.6")
    compile("org.jetbrains.exposed:exposed:0.11.2")
    compile("org.postgresql:postgresql:42.2.2")
    compile("org.litote.kmongo:kmongo:3.9.1")
    compile("org.litote.kmongo:kmongo-coroutine:3.9.1")
    compile("org.litote.kmongo:kmongo-id:3.9.1")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}