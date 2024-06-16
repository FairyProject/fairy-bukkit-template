import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    //Java plugin
    id("java-library")

    //Fairy framework plugin
    id("io.fairyproject") version "0.7.5b3-SNAPSHOT"

    // Dependency management plugin
    id("io.spring.dependency-management") version "1.1.0"

    //Kotlin plugin
    id("org.jetbrains.kotlin.jvm") version "1.9.23" apply false

    //Shadow plugin, provides the ability to shade fairy and other dependencies to compiled jar
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val libPlugin = properties("lib.plugin").toBoolean()

group = properties("group")
version = properties("version")

// Fairy configuration
fairy {
    name.set(properties("name"))
    // Main Package
    mainPackage.set(properties("package"))
    if (libPlugin) {
        fairyPackage.set("io.fairyproject")
    } else {
        // Fairy Package
        fairyPackage.set(properties("package") + ".fairy")
    }

    bukkitProperties().bukkitApi = "1.13"
}

runServer {
    version.set(properties("spigot.version"))
}

val fairy by if (libPlugin) {
    configurations.compileOnlyApi
} else {
    configurations.api
}

dependencies {
    if (libPlugin) {
        compileOnlyApi("io.fairyproject:bukkit-platform")
        api("io.fairyproject:bukkit-bootstrap")
    } else {
        api("io.fairyproject:bukkit-bundles")
    }
    fairy("io.fairyproject:mc-animation")
    fairy("io.fairyproject:bukkit-command")
    fairy("io.fairyproject:bukkit-gui")
    fairy("io.fairyproject:mc-hologram")
    fairy("io.fairyproject:core-config")
    fairy("io.fairyproject:bukkit-xseries")
    fairy("io.fairyproject:bukkit-items")
    fairy("io.fairyproject:mc-nametag")
    fairy("io.fairyproject:mc-sidebar")
    fairy("io.fairyproject:bukkit-visibility")
    fairy("io.fairyproject:bukkit-visual")
    fairy("io.fairyproject:bukkit-timer")
    fairy("io.fairyproject:bukkit-nbt")
    fairy("io.fairyproject:mc-tablist")
}

// Repositories
repositories {
    mavenCentral()
    maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))
    maven(url = uri("https://repo.codemc.io/repository/maven-public/"))
    // Spigot's repository for spigot api dependency
    maven(url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"))
    maven(url = uri("https://repo.imanity.dev/imanity-libraries"))
}

// Dependencies
dependencies {
    // Spigot dependency
    compileOnly("org.spigotmc:spigot-api:${properties("spigot.version")}-R0.1-SNAPSHOT")
}

tasks.withType(ShadowJar::class.java) {
    // Relocate fairy to avoid plugin conflict
    if (libPlugin) {
        relocate("io.fairyproject.bootstrap", "${properties("package")}.fairy.bootstrap")

        relocate("net.kyori", "io.fairyproject.libs.kyori")
        relocate("com.cryptomorin.xseries", "io.fairyproject.libs.xseries")
        relocate("org.yaml.snakeyaml", "io.fairyproject.libs.snakeyaml")
        relocate("com.google.gson", "io.fairyproject.libs.gson")
        relocate("com.github.retrooper.packetevents", "io.fairyproject.libs.packetevents")
        relocate("io.github.retrooper.packetevents", "io.fairyproject.libs.packetevents")
    } else {
        val fairyPackage = properties("package") + ".fairy"
        relocate("io.fairyproject", fairyPackage)

        relocate("net.kyori", "$fairyPackage.libs.kyori")
        relocate("com.cryptomorin.xseries", "$fairyPackage.libs.xseries")
        relocate("org.yaml.snakeyaml", "$fairyPackage.libs.snakeyaml")
        relocate("com.google.gson", "$fairyPackage.libs.gson")
        relocate("com.github.retrooper.packetevents", "$fairyPackage.libs.packetevents")
        relocate("io.github.retrooper.packetevents", "$fairyPackage.libs.packetevents")
    }
    relocate("io.fairyproject.bukkit.menu", "${properties("package")}.fairy.menu")
}