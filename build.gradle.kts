plugins {
    id("java")
    id("com.github.ben-manes.versions") version "0.51.0"
}

repositories {
    mavenCentral()
    google()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}
sourceSets {
    main {
        java {
            setSrcDirs(listOf("Ollivanders/src"))
        }
        resources {
            setSrcDirs(listOf("Ollivanders/src"))
        }
    }
    test {
        java {
            setSrcDirs(listOf("Ollivanders/test/java"))
        }
        resources {
            srcDirs("Ollivanders/test/resources")
        }
    }
}
dependencies {
    implementation("org.jetbrains:annotations:26.0.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")

    // local plugin jars
    implementation(files("/Users/kristin/minecraft/plugins/worldguard-bukkit-7.0.9-dist.jar"))
    implementation(files("/Users/kristin/minecraft/plugins/worldedit-bukkit-7.3.9.jar"))
    implementation(files("/Users/kristin/minecraft/plugins/ProtocolLib-5.4.0.jar"))
    implementation(files("/Users/kristin/minecraft/plugins/LibsDisguises-11.0.7-Premium.jar"))

    testImplementation(files("/Users/kristin/minecraft/plugins/worldguard-bukkit-7.0.9-dist.jar"))
    testImplementation(files("/Users/kristin/minecraft/plugins/worldedit-bukkit-7.3.9.jar"))
    testImplementation(files("/Users/kristin/minecraft/plugins/ProtocolLib-5.4.0.jar"))
    testImplementation(files("/Users/kristin/minecraft/plugins/LibsDisguises-11.0.7-Premium.jar"))

    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // mockbukkit
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.71.0")
    testImplementation("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")

    // allow tests to use your plugin classes
    testImplementation(sourceSets.main.get().output)
}
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-deprecation")
    options.compilerArgs.add("-Xlint:-unchecked")
}

tasks.test {
    useJUnitPlatform()
    // Make sure MockBukkit is completely cleaned up
    finalizedBy("cleanTestPluginsDir")
}

tasks.register<Delete>("cleanTestPluginsDir") {
    delete("plugins")
}
