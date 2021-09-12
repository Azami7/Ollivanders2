#Ollivanders2

You've been accepted to MC Hogwarts School of Witchcraft and Wizardy.

##What is Ollivanders2?
Ollivanders2 is reboot of the Ollivanders Bukkit plugin which adds the magic from Harry Potter to your Minecraft server.

##Server Admins
For more information running Ollivanders2 on your server and to download Ollivanders2 on your server, see our 
[Spigot Page](https://www.spigotmc.org/resources/ollivanders2.38992/) and 
[Ollivanders2 Wiki on GitHub](https://github.com/Azami7/Ollivanders2/wiki).

###Important notes for running Ollivanders2 ==> READ THIS <==
1. Ollvanders2 requires your server is running with Java 11. Earlier versions will not work and the plugin will not load.
2. Verify that the version of Ollivanders2 you have matches the version of MC you are running. Mismatched versions will 
either cause the plugin to unload (if it detects a MC version too low) or missing/broken features (if your MC is too high).

###Configuring Ollivanders2
Ollivanders2 comes configured by default with the least intrusive options for your server. You can edit the 
`````config.yml````` file in `````/plugins/Ollivanders2````` to change these settings.

Be sure to read the [Documentation for Ollivanders2 config](https://github.com/Azami7/Ollivanders2/wiki/Configuration) 
before making changes.

##Discord
Want to talk to the plugin designers, other server admins, or need support? Join our 
[Discord Server](https://discord.gg/ANWvCWeQ96).

##Development
Current code repository at https://github.com/Azami7/Ollivanders2

###Building Ollivanders2
Java 11 is required to build Ollivanders2.

Additional libraries you will need to build Ollivanders2:
* gson 2.6.2 
* spigot 1.16.5
* worldedit 7.2.6 
* worldguard 7.0.4 
* jetbrains annotations 17.0.0
* libsdisguises 10.0.25 

In IntelliJ:
1. File -> Project Structure -> Project
1. Set Project SDK to your version of Java 11 (we use Corretto)
1. File -> Project Structure -> Modules
1. Set Module SDK to your version of Java 11

Stop here and try to build the project. If you don't see a ton of errors (should be every file), your SDK is not 
configured correctly.  

1. File -> Project Structure -> Modules
1. Add the above listed libraries to the module list

Build again and everything should be happy. Yay!

###Creating the Ollivanders2 jar
Creating the jar for Ollivanders is pretty simple. All you will need is the compiled source plus the gson-2.6.2 jar. 

In IntelliJ:
1. File -> Project Structure -> Artifacts
1. Make sure 'Ollivanders2' compiled source is listed. Note: you must have built successfully for this option to appear.
1. Add the gson jar
1. Build -> Build Artifacts -> Build

If this worked, IntelliJ will have created an `````/out/artifacts````` directory in the top level of your Idea project directory.

