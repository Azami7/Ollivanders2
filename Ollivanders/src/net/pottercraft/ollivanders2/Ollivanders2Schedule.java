package net.pottercraft.ollivanders2;

import java.util.ArrayList;
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
 * Main scheduler for Ollivanders2.
 *
 * <p>This scheduler runs every game tick and is responsible for the recurring upkeep of all active
 * game systems including spell projectiles, player effects, stationary spells, prophecies, and
 * pending teleport actions. Additionally, it manages periodic tasks such as invisibility cloak
 * visibility checks (every second) and plugin data persistence (hourly).</p>
 *
 * @author Azami7
 * @version Ollivanders2
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
     * Reset the counter after this many ticks to prevent it growing unbounded
     */
    private final static int timerReset = 86400 * Ollivanders2Common.ticksPerSecond;

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    Ollivanders2Schedule(@NotNull Ollivanders2 plugin) {
        p = plugin;
    }

    /**
     * Primary plugin scheduler that runs every game tick.
     *
     * <p>Executes the following operations:</p>
     * <ul>
     * <li>Updates active spell projectiles via O2Spells.upkeep()</li>
     * <li>Updates player effects</li>
     * <li>Updates stationary spells</li>
     * <li>Updates prophecies</li>
     * <li>Processes pending teleport actions</li>
     * <li>Handles invisibility cloak visibility every second</li>
     * <li>Saves plugin data hourly</li>
     * </ul>
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
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("Exceoption running scheduled tasks.", e, null, true);
        }

        // run invisibility functions every second, offset from itemCurse schedule
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
     * Calls upkeep on player effects for all online players.
     *
     * <p>This triggers the effect system to update all active effects and remove those marked for removal.</p>
     */
    private void effectScheduler() {
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
     * Handles hiding and revealing players when they wear an invisibility cloak. We cannot make this an
     * item enchantment that can listen to events and react because MC doesn't have an event for a player
     * equipping an item, lame.
     */
    private void handleInvisibilityCloaks() {
        for (Player player : p.getServer().getOnlinePlayers()) {
            boolean wearingInvisibilityCloak = O2PlayerCommon.wearingInvisibilityCloak(player);
            boolean hasInvisibilityPotionEffect = O2PlayerCommon.hasPotionEffect(player, PotionEffectType.INVISIBILITY);

            // if they are wearing the cloak but not invisible, make them invisible
            if (wearingInvisibilityCloak && !hasInvisibilityPotionEffect) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            }
            // if they are not wearing the cloak but are invisible, make them visible
            else if (!wearingInvisibilityCloak && hasInvisibilityPotionEffect) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    /**
     * Processes all pending teleport actions.
     *
     * <p>For each pending teleport action, this method:</p>
     * <ul>
     * <li>Preserves the player's original pitch and yaw orientation</li>
     * <li>Teleports the player to the destination location</li>
     * <li>Creates explosion effects at both origin and destination (if enabled)</li>
     * <li>Removes the action from the pending teleport queue</li>
     * </ul>
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
                    Ollivanders2API.common.printDebugMessage("OllvandersSchedule.teleportSched: world is null", null, null, true);
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