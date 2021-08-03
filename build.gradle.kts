import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val decomposeVersion = "0.2.6"
val exposedVersion = "0.32.1"
val koinVersion = "3.1.2"

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.compose") version "0.4.0"
    id("org.jmailen.kotlinter") version "3.4.5"
}

group = "me.mes"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.30.1")

    // for logging (StdOutSqlLogger), see
    // http://www.slf4j.org/codes.html#StaticLoggerBinder
    implementation("org.slf4j:slf4j-nop:1.7.30")

    implementation("io.insert-koin:koin-core:$koinVersion")
//    testImplementation("io.insert-koin:koin-test:$koinVersion")
//    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")
}

kotlinter {
    indentSize = 2
    disabledRules = arrayOf("no-wildcard-imports")
}

tasks.check {
    dependsOn("installKotlinterPrePushHook")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "todos"
            packageVersion = "1.0.0"
        }
    }
}