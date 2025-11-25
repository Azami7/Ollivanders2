package net.pottercraft.ollivanders2.test.pluginDependencies;

import org.bukkit.plugin.java.JavaPlugin;

public class LibsDisguisesMock extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("LibsDisguises mock loaded");
    }
}
