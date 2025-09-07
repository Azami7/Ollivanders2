plugins {
    id("java")
}

repositories {
    mavenCentral()
    google()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://mvnrepository.com/artifact/org.mockbukkit.mockbukkit/")
    maven("https://repo.papermc.io/repository/maven-public/")
}
sourceSets {
    main {
        java {
            setSrcDirs(listOf("Ollivanders/src"))
        }
        resources {
            setSrcDirs(listOf("Ollivanders/src"))
            resources.srcDirs("Ollivanders/test/resources")
            // Optional: only expose plugin-test.yml to tests
            resources.include("plugin-test.yml")
        }
    }
    test {
        java {
            setSrcDirs(listOf("Ollivanders/test/java"))
        }
    }
}
dependencies {
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation(files("/Users/kristin/minecraft/plugins/worldguard-bukkit-7.0.9-dist.jar"))
    implementation(files("/Users/kristin/minecraft/plugins/worldedit-bukkit-7.3.9.jar"))
    implementation(files("/Users/kristin/minecraft/plugins/ProtocolLib-5.4.0.jar"))
    implementation(files("/Users/kristin/minecraft/plugins/LibsDisguises-11.0.7-Premium.jar"))
    
    testImplementation(files("/Users/kristin/minecraft/plugins/worldguard-bukkit-7.0.9-dist.jar"))
    testImplementation(files("/Users/kristin/minecraft/plugins/worldedit-bukkit-7.3.9.jar"))
    testImplementation(files("/Users/kristin/minecraft/plugins/ProtocolLib-5.4.0.jar"))
    testImplementation(files("/Users/kristin/minecraft/plugins/LibsDisguises-11.0.7-Premium.jar"))

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.76.0")
    testImplementation(sourceSets.main.get().output)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
}
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-deprecation")
    options.compilerArgs.add("-Xlint:-unchecked")
}

tasks.test {
    useJUnitPlatform()
}
