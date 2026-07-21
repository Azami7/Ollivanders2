package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nullum Apparebit: a stationary spell that blocks apparition (by name or coordinates) and teleportation into its area,
 * making it a sanctuary that cannot be entered by magical travel.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">Harry Potter Wiki - Anti-Disapparition Jinx</a>
 */
public class NULLUM_APPAREBIT extends O2StationarySpell {
    /**
     * The message shown to a player blocked from apparating or teleporting into the area.
     */
    public static final String feedbackMessage = "A powerful magic protects that place.";

    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 50;

    /**
     * Minimum spell duration: 5 minutes.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration: 30 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.NULLUM_APPAREBIT;
    }

    /**
     * Constructor for casting a new Nullum Apparebit spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.NULLUM_APPAREBIT;

        setRadius(radius);
        setDuration(duration, false);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    @Override
    public void upkeep() {
        age();
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cancel apparition-by-name into the area and give the player feedback.
     *
     * @param event the apparate by name event
     */
    @Override
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        Location destination = event.getDestination();

        if (isLocationInside(destination)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_APPAREBIT: canceled OllivandersApparateByNameEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(event.getPlayer());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Cancel apparition-by-coordinates into the area and give the player feedback.
     *
     * @param event the apparate by coordinates event
     */
    @Override
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        Location destination = event.getDestination();

        if (isLocationInside(destination)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_APPAREBIT: canceled OllivandersApparateByCoordinatesEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(event.getPlayer());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Cancel a non-player entity teleporting into the area.
     *
     * @param event the entity teleport event
     */
    @Override
    void doOnEntityTeleportEvent(@NotNull EntityTeleportEvent event) {
        Location destination = event.getTo();
        if (destination == null)
            return;

        if (isLocationInside(destination)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_APPAREBIT: canceled EntityTeleportEvent", null, null, false);
        }
    }

    /**
     * Cancel a player teleporting into the area and give them feedback.
     *
     * @param event the player teleport event
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        Location destination = event.getTo();
        if (destination == null)
            return;

        if (isLocationInside(destination)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_APPAREBIT: canceled PlayerTeleportEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(event.getPlayer());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Tell a blocked player the area is protected, with a message, sound, and flair.
     *
     * @param player the player who was blocked
     */
    private void playerFeedback(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + feedbackMessage);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        flair(5);
    }

    @Override
    void doCleanUp() {
    }
}