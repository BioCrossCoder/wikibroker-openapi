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

    val okhttpVersion = "4.12.0"
    // Source: https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    compileOnly("com.squareup.okhttp3:okhttp:$okhttpVersion")
    testImplementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    val ktorVersion = "3.4.2"
    // Source: https://mvnrepository.com/artifact/io.ktor/ktor-client-core
    compileOnly("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    // Source: https://mvnrepository.com/artifact/io.ktor/ktor-client-mock
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")

    val kotlinxVersion = "1.11.0"
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxVersion")

    val cryptoVersion = "0.6.0"
    implementation("dev.whyoleg.cryptography:cryptography-core:$cryptoVersion")
    implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:$cryptoVersion")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}