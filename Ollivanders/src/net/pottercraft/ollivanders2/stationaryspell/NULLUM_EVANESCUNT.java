package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nullum evanescunt creates a stationary spell which will not allow apparition out of it.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.NULLUM_EVANESCUNT}
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx</a>
 */
public class NULLUM_EVANESCUNT extends O2StationarySpell {
    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public NULLUM_EVANESCUNT(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.NULLUM_EVANESCUNT;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public NULLUM_EVANESCUNT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.NULLUM_EVANESCUNT;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    void initRadiusAndDurationMinMax() {
        // make min/max radius and duration the same as Nullum Apparebit
        minRadius = NULLUM_APPAREBIT.minRadiusConfig;
        maxRadius = NULLUM_APPAREBIT.maxRadiusConfig;
        minDuration = NULLUM_APPAREBIT.minDurationConfig;
        maxDuration = NULLUM_APPAREBIT.maxDurationConfig;
    }

    /**
     * Age the spell by 1 tick
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Prevent player apparating to this location
     *
     * @param event the apparate event
     */
    @Override
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        Player player = event.getPlayer(); // will never be null
        Location playerLocation = player.getLocation();

        if (isLocationInside(playerLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled OllivandersApparateByNameEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Prevent player apparating to this location
     *
     * @param event the apparate event
     */
    @Override
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        Player player = event.getPlayer(); // will never be null
        Location playerLocation = player.getLocation();

        if (isLocationInside(playerLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled OllivandersApparateByCoordinatesEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Prevent entity teleporting to this location
     *
     * @param event the teleport event
     */
    @Override
    void doOnEntityTeleportEvent(@NotNull EntityTeleportEvent event) {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation();

        if (isLocationInside(entityLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled EntityTeleportEvent", null, null, false);
        }
    }

    /**
     * Prevent player teleporting to this location
     *
     * @param event the teleport event
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer(); // will never be null
        Location playerLocation = player.getLocation();

        if (isLocationInside(playerLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled PlayerTeleportEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Feedback to the player when they try to apparate.
     *
     * @param player the player trying to teleport/apparate
     */
    private void playerFeedback(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "A powerful magic protects this place.");
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        flair(5);
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    @Override
    void doCleanUp() {
    }
}