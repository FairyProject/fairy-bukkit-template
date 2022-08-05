import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    //Java plugin
    id("java")

    //Kotlin plugin
    // id("org.jetbrains.kotlin.jvm") version "1.7.10"

    //Shadow plugin, provides the ability to shade fairy and other dependencies to compiled jar
    id("com.github.johnrengelman.shadow") version "7.1.2"

    //Fairy framework plugin
    id("io.fairyproject") version "1.3.0b2"
}

group = properties("group")
version = properties("version")

// Fairy configuration
fairy {
    //Fairy version
    version.set(properties("fairy.version"))
    //Main Package
    mainPackage.set(properties("package"))
    //Install bukkit platform to this project.
    platform("bukkit")

    //Should fairy framework be compiled for runtime (by default it's compileOnly)
    doCompile()
    //Should bootstrap be installed for this project
    doBootstraps()
    //Should test tool kits be installed for this project
    // doTests()

    //Modules
    // module("command")
}

// Repositories
repositories {
    mavenCentral()
    // Spigot's repository for spigot api dependency
    maven(url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"))
}

// Dependencies
dependencies {
    // Spigot dependency
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.withType(ShadowJar::class.java) {
    // Relocate fairy to avoid plugin conflict
    relocate("io.fairyproject", "${properties("package")}.fairy")
}