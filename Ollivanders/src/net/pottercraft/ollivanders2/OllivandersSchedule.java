package net.pottercraft.ollivanders2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.jetbrains.annotations.NotNull;

/**
 * Main scheduler for Ollivanders2
 */
public class OllivandersSchedule implements Runnable {
    /**
     * A callback to the plugin
     */
    final private Ollivanders2 p;

    /**
     * Counts game ticks
     */
    private int scheduleTimer = 0;

    /**
     * Reset the counter after this many ticks to prevent it growing unbounded
     */
    private final static int timerReset = 86400 * Ollivanders2Common.ticksPerSecond;

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    OllivandersSchedule(@NotNull Ollivanders2 plugin) {
        p = plugin;
    }

    /**
     * Primary plugin thread
     */
    public void run() {
        // run every tick
        try {
            projectileSched();
            oeffectSched();
            Ollivanders2API.getStationarySpells().upkeep();
            Ollivanders2API.getProphecies().upkeep();
            teleportSched();
            Ollivanders2API.getOwlPost().upkeep();
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("Exceoption running scheduled tasks.", e, null, true);
        }

        // run invis functions every second, offset from itemCurse schedule
        if (scheduleTimer % Ollivanders2Common.ticksPerSecond == 1) {
            handleInvisibilityCloaks();
        }

        // back up plugin data hourly
        if (Ollivanders2.hourlyBackup && scheduleTimer % Ollivanders2Common.ticksPerHour == 0) {
            Ollivanders2API.common.printDebugMessage("Saving plugin data...", null, null, false);

            p.savePluginData();
        }

        // Reset the timer so it does not grow unbounded, use >= just in case a tick gets missed somehow
        if (scheduleTimer >= timerReset)
            scheduleTimer = 1;
        else
            scheduleTimer = scheduleTimer + 1;
    }

    /**
     * Scheduling method that calls checkEffect() on all SpellProjectile objects
     * and removes those that have kill set to true.
     */
    private void projectileSched() {
        List<O2Spell> projectiles = p.getProjectiles();
        List<O2Spell> projectiles2 = new ArrayList<>(projectiles);
        if (projectiles2.size() > 0) {
            for (O2Spell proj : projectiles2) {
                proj.checkEffect();
                if (proj.isKilled()) {
                    p.removeProjectile(proj);
                }
            }
        }
    }

    /**
     * Scheduling method that calls checkEffect on all OEffect objects associated with every online player
     * and removes those that have kill set to true.
     */
    private void oeffectSched() {
        List<Player> onlinePlayers = new ArrayList<>();

        for (World world : p.getServer().getWorlds()) {
            onlinePlayers.addAll(world.getPlayers());
        }

        for (Player player : onlinePlayers) {
            UUID pid = player.getUniqueId();

            Ollivanders2API.getPlayers().playerEffects.upkeep(pid);
        }
    }

    /**
     * Handles hiding and revealing players when they wear an invisibilty cloak. We cannot make this an
     * item enchantment that can listen to events and react because MC doesn't have an event for a player
     * equipping an item, lame.
     */
    private void handleInvisibilityCloaks() {
        for (Player player : p.getServer().getOnlinePlayers()) {
            boolean wearingInvisbilityCloak = wearingInvisibilityCloak(player);
            boolean hasInvisiblityPotionEffect = O2PlayerCommon.hasPotionEffect(player, PotionEffectType.INVISIBILITY);

            // if they are wearing the cloak but not invisible, make them invisible
            if (wearingInvisbilityCloak && !hasInvisiblityPotionEffect) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            }
            // if they are not wearing the cloak but are invisible, make them visible
            else if (!wearingInvisbilityCloak && hasInvisiblityPotionEffect) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    /**
     * Does the player have the Cloak of Invisibility
     *
     * @param player player to be checked
     * @return true if yes, false if no
     */
    private boolean wearingInvisibilityCloak(@NotNull Player player) {
        ItemStack chestPlate = player.getInventory().getChestplate();
        if (chestPlate != null) {
            return O2ItemType.INVISIBILITY_CLOAK.isItemThisType(chestPlate);
        }
        return false;
    }

    /**
     * Handle all teleport events.
     */
    private void teleportSched() {
        List<Ollivanders2TeleportEvents.O2TeleportEvent> teleportEvents = p.getTeleportEvents();

        for (Ollivanders2TeleportEvents.O2TeleportEvent event : teleportEvents) {
            Player player = event.getPlayer();

            Ollivanders2API.common.printDebugMessage("Teleporting " + player.getName(), null, null, false);

            Location currentLocation = event.getFromLocation();
            Location destination = event.getToLocation();
            destination.setPitch(currentLocation.getPitch());
            destination.setYaw(currentLocation.getYaw());

            try {
                player.teleport(destination);

                World curWorld = currentLocation.getWorld();
                World destWorld = destination.getWorld();
                if (curWorld == null || destWorld == null) {
                    Ollivanders2API.common.printDebugMessage("OllvandersSchedule.teleportSched: world is null", null, null, true);
                }
                else {
                    if (event.isExplosionOnTeleport()) {
                        curWorld.createExplosion(currentLocation, 0);
                        destWorld.createExplosion(destination, 0);
                    }
                }
            }
            catch (Exception e) {
                Ollivanders2API.common.printDebugMessage("Failed to teleport player.", e, null, true);
            }

            p.removeTeleportEvent(event);
        }
    }
}