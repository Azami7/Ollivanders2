package net.pottercraft.ollivanders2;

import java.util.List;
import java.util.UUID;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.jetbrains.annotations.NotNull;

/**
 * Main scheduler for Ollivanders2. Runs every game tick and drives the recurring upkeep of all active game
 * systems, invisibility cloak visibility checks every second, and plugin data saves hourly when enabled.
 *
 * @author Azami7
 */
public class Ollivanders2Schedule implements Runnable {
    /**
     * A callback to the plugin
     */
    final private Ollivanders2 p;

    /**
     * Counts game ticks
     */
    private int scheduleTimer = 0;

    /**
     * Reset the counter after this many ticks (24 real-time hours) to prevent it growing unbounded
     */
    private final static int timerReset = 24 * Ollivanders2Common.ticksPerHour;

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    Ollivanders2Schedule(@NotNull Ollivanders2 plugin) {
        p = plugin;
    }

    /**
     * Run one game tick of plugin upkeep: per-tick upkeep for every game system, invisibility cloak
     * visibility checks once a second, and an hourly plugin data save when hourly backup is enabled.
     */
    public void run() {
        // run every tick
        try {
            Ollivanders2API.getSpells().upkeep();
            effectScheduler();
            Ollivanders2API.getStationarySpells().upkeep();
            Ollivanders2API.getProphecies().upkeep();
            teleportScheduler();
            Ollivanders2API.getOwlPost().upkeep();

            // run invisibility functions every second, offset by a tick so they never share a tick with the hourly save
            if (scheduleTimer % Ollivanders2Common.ticksPerSecond == 1) {
                handleInvisibilityCloaks();
            }

            // back up plugin data hourly
            if (Ollivanders2.hourlyBackup && scheduleTimer % Ollivanders2Common.ticksPerHour == 0) {
                Ollivanders2API.common.printDebugMessage("Saving plugin data...", null, null, false);

                p.savePluginData();
            }

        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("Exception running scheduled tasks.", e, null, true);
        }

        // Reset the timer so it does not grow unbounded, use >= just in case a tick gets missed somehow. Reset
        // to 1 rather than 0 so the hourly save does not immediately re-fire on the tick after it just ran.
        if (scheduleTimer >= timerReset)
            scheduleTimer = 1;
        else
            scheduleTimer = scheduleTimer + 1;
    }

    /**
     * Call effects upkeep for every online player.
     */
    private void effectScheduler() {
        for (Player player : p.getServer().getOnlinePlayers()) {
            UUID pid = player.getUniqueId();

            Ollivanders2API.getPlayers().playerEffects.upkeep(pid);
        }
    }

    /**
     * Hide players wearing an invisibility cloak and reveal them when they take it off. Runs on a schedule
     * rather than reacting to events because Minecraft has no event for a player equipping an item.
     */
    private void handleInvisibilityCloaks() {
        for (Player player : p.getServer().getOnlinePlayers()) {
            boolean wearingInvisibilityCloak = O2PlayerCommon.wearingInvisibilityCloak(player);
            boolean hasInvisibilityCloakEffect = hasInvisibilityCloakEffect(player);

            // if they are wearing the cloak but not invisible, make them invisible
            if (wearingInvisibilityCloak && !hasInvisibilityCloakEffect) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1));
            }

            // if they are not wearing the cloak and do not have other invisibility but are invisible, make them visible
            else if (!wearingInvisibilityCloak && hasInvisibilityCloakEffect) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    /**
     * Check whether a player's invisibility came from wearing an invisibility cloak. The cloak grants an
     * infinite-duration effect while potion-granted invisibility is always finite, so a potion-granted
     * effect is never mistaken for the cloak's.
     *
     * @param player the player to check
     * @return true if the player has an infinite invisibility effect, false otherwise
     */
    private boolean hasInvisibilityCloakEffect(@NotNull Player player) {
        if (O2PlayerCommon.hasPotionEffect(player, PotionEffectType.INVISIBILITY)) {
            PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.INVISIBILITY);

            return potionEffect.isInfinite(); // ignore the linting warning, hasPotionEffect check ensures this is not null
        }

        return false;
    }

    /**
     * Execute and clear all pending teleport actions, preserving each player's facing direction and creating
     * explosion effects at the origin and destination when the action requests them.
     */
    private void teleportScheduler() {
        List<Ollivanders2TeleportActions.O2TeleportAction> teleportActions = p.getTeleportActions();

        for (Ollivanders2TeleportActions.O2TeleportAction action : teleportActions) {
            Player player = action.getPlayer();

            Ollivanders2API.common.printDebugMessage("Teleporting " + player.getName(), null, null, false);

            Location currentLocation = action.getFromLocation();
            Location destination = action.getToLocation();
            destination.setPitch(currentLocation.getPitch());
            destination.setYaw(currentLocation.getYaw());

            try {
                player.teleport(destination);

                World curWorld = currentLocation.getWorld();
                World destWorld = destination.getWorld();
                if (curWorld == null || destWorld == null) {
                    Ollivanders2API.common.printDebugMessage("Ollivanders2Schedule.teleportScheduler: world is null", null, null, true);
                }
                else {
                    if (action.isExplosionOnTeleport()) {
                        curWorld.createExplosion(currentLocation, 0);
                        destWorld.createExplosion(destination, 0);
                    }
                }
            }
            catch (Exception e) {
                Ollivanders2API.common.printDebugMessage("Failed to teleport player.", e, null, true);
            }

            p.removeTeleportAction(action);
        }
    }
}