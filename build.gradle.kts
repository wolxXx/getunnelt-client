import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
    kotlin("plugin.serialization") version "1.9.21"
    //application
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.java-websocket:Java-WebSocket:1.5.3") // falls du WS nativ machen willst
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

}


compose.desktop {
    application {
        mainClass = "AppKt"
        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Msi,
                TargetFormat.AppImage,
                TargetFormat.Dmg,

            )
            packageName = "tunnelclient"
            packageVersion = "1.0.0"
            linux {
                shortcut = true
                menuGroup = "Network Tools"
            }
        }
    }
}