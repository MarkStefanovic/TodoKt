import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val exposedVersion = "0.36.1"
val koinVersion = "3.1.4"

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
    id("org.jmailen.kotlinter") version "3.8.0"
}

group = "me.mes"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")

    // for logging (StdOutSqlLogger), see
    // http://www.slf4j.org/codes.html#StaticLoggerBinder
    implementation("org.slf4j:slf4j-nop:1.7.32")

    implementation("io.insert-koin:koin-core:$koinVersion")
//    testImplementation("io.insert-koin:koin-test:$koinVersion")
//    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")

    implementation("androidx.annotation:annotation:1.3.0")

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

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "16"
}

tasks.compileJava {
    targetCompatibility = "16"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.sql")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "todos"
            packageVersion = "1.0.0"
        }
    }
}