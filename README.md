# Ollivanders2
Updated version of the Ollivander's plugin for Spigot

# Documentation for server admins
[Ollivanders2 wiki](https://github.com/Azami7/Ollivanders2/wiki)

# Javadoc
[Ollivanders2 Javadoc](https://pottercraft.s3.us-west-2.amazonaws.com/Ollivanders2/javadoc/index.html)

## What is Ollivanders2?
Ollivanders2 is reboot of the Ollivanders Bukkit plugin which adds the magic from Harry Potter to your Minecraft server.

## Server Admins
For more information running Ollivanders2 on your server and to download Ollivanders2 on your server, see our 
[Spigot Page](https://www.spigotmc.org/resources/ollivanders2.38992/) and 
[Ollivanders2 Wiki on GitHub](https://github.com/Azami7/Ollivanders2/wiki).

### Important notes for running Ollivanders2 ==> READ THIS <==
1. Ollvanders2 requires your server is running with Java 11. Earlier versions will not work and the plugin will not load.
2. Verify that the version of Ollivanders2 you have matches the version of MC you are running. Mismatched versions will 
either cause the plugin to unload (if it detects a MC version too low) or missing/broken features (if your MC is too high).

### Configuring Ollivanders2
Ollivanders2 comes configured by default with the least intrusive options for your server. You can edit the 
`````config.yml````` file in `````/plugins/Ollivanders2````` to change these settings.

Be sure to read the [Documentation for Ollivanders2 config](https://github.com/Azami7/Ollivanders2/wiki/Configuration) 
before making changes.

## Discord
Want to talk to the plugin designers, other server admins, or need support? Join our 
[Discord Server](https://discord.gg/ANWvCWeQ96).

## Development
Current code repository at https://github.com/Azami7/Ollivanders2

### Building Ollivanders2
Java 17 is required to build Ollivanders2.

### Here is how to set the correct SDK
IntelliJ: https://www.jetbrains.com/help/idea/sdk.html

Eclipse: https://wiki.eclipse.org/Eclipse_Platform_SDK_Provisioning

### Creating the Ollivanders2 jar
Creating the jar for Ollivanders2 is pretty simple.

Maven Goal: clean package

IntelliJ: https://www.jetbrains.com/help/idea/delegate-build-and-run-actions-to-maven.html

Eclipse: https://www.vogella.com/tutorials/EclipseMaven/article.html
