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
 * A stationary spell that prevents apparition and teleportation into the protected area.
 *
 * <p>Nullum Apparebit creates a powerful anti-apparition barrier that prevents outside entities
 * from accessing the protected area through magical means. The spell blocks:
 * <ul>
 *   <li>Apparation by name into the spell area</li>
 *   <li>Apparation by coordinates into the spell area</li>
 *   <li>Teleportation into the spell area</li>
 * </ul>
 * </p>
 *
 * <p>This spell creates a magical sanctuary that cannot be breached by apparition or teleportation.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx</a>
 */
public class NULLUM_APPAREBIT extends O2StationarySpell {
    /**
     * The message a player gets if they try to teleport or apparate in to the spell area
     */
    public static final String feedbackMessage = "A powerful magic protects that place.";

    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (50 blocks).
     */
    public static final int maxRadiusConfig = 50;

    /**
     * Minimum spell duration (5 minutes).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration (30 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.NULLUM_APPAREBIT;
    }

    /**
     * Constructs a new NULLUM_APPAREBIT spell cast by a player.
     *
     * <p>Creates an anti-apparition barrier at the specified location with the given radius and
     * duration. Outside entities cannot access the protected area through apparition or teleportation.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.NULLUM_APPAREBIT;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-50 blocks) and duration boundaries (5 to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Ages the spell by one tick.
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Serializes the nullum apparebit spell data for persistence.
     *
     * <p>The spell has no extra data to serialize beyond the base spell properties,
     * so this method returns an empty map.</p>
     *
     * @return an empty map (the spell has no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes nullum apparebit spell data from saved state.
     *
     * <p>The spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Prevents players from apparating into the spell area by name.
     *
     * <p>When a player attempts to apparate by name into the protected area, the apparition
     * is cancelled and they receive feedback that the area is magically protected.</p>
     *
     * @param event the apparate by name event (not null)
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
     * Prevents players from apparating into the spell area by coordinates.
     *
     * <p>When a player attempts to apparate by coordinates into the protected area, the apparition
     * is cancelled and they receive feedback that the area is magically protected.</p>
     *
     * @param event the apparate by coordinates event (not null)
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
     * Prevents entities from teleporting into the spell area.
     *
     * <p>When an entity attempts to teleport into the protected area, the teleportation is cancelled,
     * preventing access to the sanctuary.</p>
     *
     * @param event the entity teleport event (not null)
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
     * Prevents players from teleporting into the spell area.
     *
     * <p>When a player attempts to teleport into the protected area, the teleportation is cancelled
     * and they receive feedback that the area is magically protected.</p>
     *
     * @param event the player teleport event (not null)
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
     * Provides feedback to a player when they attempt to access the protected area.
     *
     * <p>Sends a message, plays a sound effect, and displays a visual flair to indicate
     * the spell is preventing their access.</p>
     *
     * @param player the player attempting to apparate or teleport into the protected area (not null)
     */
    private void playerFeedback(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + feedbackMessage);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        flair(5);
    }

    /**
     * Cleans up when the nullum apparebit spell ends.
     *
     * <p>The spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}