plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"
}

group = "com.wikiglobal"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    // Source: https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Source: https://mvnrepository.com/artifact/io.ktor/ktor-client-core
    compileOnly("io.ktor:ktor-client-core:3.4.2")
    testImplementation("io.ktor:ktor-client-core:3.4.2")
    // Source: https://mvnrepository.com/artifact/io.ktor/ktor-client-mock
    testImplementation("io.ktor:ktor-client-mock:3.4.2")
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}