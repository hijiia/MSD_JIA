val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20"
}

group = "com.fancycamera"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnit()
}

dependencies {
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-auth-ldap")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-utils")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("at.favre.lib:bcrypt:0.10.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-content-negotiation")
}
