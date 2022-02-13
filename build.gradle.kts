import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val exposedVersion = "0.37.3"
val koinVersion = "3.1.5"

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev606"
    id("org.jmailen.kotlinter") version "3.8.0"
    id("com.github.ben-manes.versions") version "0.42.0"
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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    // for logging (StdOutSqlLogger), see
    // http://www.slf4j.org/codes.html#StaticLoggerBinder
    implementation("org.slf4j:slf4j-nop:2.0.0-alpha6")

    implementation("androidx.annotation:annotation:1.4.0-alpha02")

}

kotlinter {
    indentSize = 2
    disabledRules = arrayOf("no-wildcard-imports")
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }
    compileJava {
        targetCompatibility = "16"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.instrument", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "todos"
            packageVersion = "1.0.0"
        }
    }
}