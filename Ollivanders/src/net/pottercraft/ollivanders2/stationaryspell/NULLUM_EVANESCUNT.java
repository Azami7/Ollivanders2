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
 * A stationary spell that prevents apparition and teleportation out of the protected area.
 *
 * <p>Nullum Evanescunt creates a powerful anti-disapparition barrier that prevents entities
 * from escaping through magical means. Those trapped inside cannot:
 * <ul>
 *   <li>Apparate by name to leave the spell area</li>
 *   <li>Apparate by coordinates to leave the spell area</li>
 *   <li>Teleport away from the spell area</li>
 * </ul>
 * </p>
 *
 * <p>Note: This spell uses min/max constraints from {@link NULLUM_APPAREBIT}.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx</a>
 */
public class NULLUM_EVANESCUNT extends O2StationarySpell {
    /**
     * The message a player receives if they try to teleport or apparate out of the area
     */
    public static final String feedbackMessage = "A powerful magic protects this place.";

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
     * Constructs a new 1 - spell cast by a player.
     *
     * <p>Creates an anti-disapparition barrier at the specified location with the given radius
     * and duration. Entities inside the protected area cannot escape through apparition or
     * teleportation magic.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public NULLUM_EVANESCUNT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.NULLUM_EVANESCUNT;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Uses the same constraints as {@link NULLUM_APPAREBIT}:
     * <ul>
     *   <li>Radius: 5-50 blocks</li>
     *   <li>Duration: 5-30 minutes</li>
     * </ul>
     * </p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        // make min/max radius and duration the same as Nullum Apparebit
        minRadius = NULLUM_APPAREBIT.minRadiusConfig;
        maxRadius = NULLUM_APPAREBIT.maxRadiusConfig;
        minDuration = NULLUM_APPAREBIT.minDurationConfig;
        maxDuration = NULLUM_APPAREBIT.maxDurationConfig;
    }

    /**
     * Ages the spell by one tick.
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Prevents players from apparating away by name while inside the spell area.
     *
     * <p>When a player inside the protected area attempts to apparate by name, the apparition
     * is cancelled and they receive feedback that the area is magically protected.</p>
     *
     * @param event the apparate by name event (not null)
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
     * Prevents players from apparating away by coordinates while inside the spell area.
     *
     * <p>When a player inside the protected area attempts to apparate by coordinates, the apparition
     * is cancelled and they receive feedback that the area is magically protected.</p>
     *
     * @param event the apparate by coordinates event (not null)
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
     * Prevents entities from teleporting away while inside the spell area.
     *
     * <p>When an entity inside the protected area attempts to teleport away, the teleportation
     * is cancelled, trapping the entity inside the area.</p>
     *
     * @param event the entity teleport event (not null)
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
     * Prevents players from teleporting away while inside the spell area.
     *
     * <p>When a player inside the protected area attempts to teleport away, the teleportation
     * is cancelled, and they receive feedback that the area is magically protected.</p>
     *
     * @param event the player teleport event (not null)
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
     * Provides feedback to a player when they attempt to escape the protected area.
     *
     * <p>Sends a message, plays a sound effect, and displays a visual flair to indicate
     * the spell is preventing their escape.</p>
     *
     * @param player the player attempting to apparate or teleport away (not null)
     */
    private void playerFeedback(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + feedbackMessage);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        flair(5);
    }

    /**
     * Serializes the nullum evanescunt spell data for persistence.
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
     * Deserializes nullum evanescunt spell data from saved state.
     *
     * <p>The spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the nullum evanescunt spell ends.
     *
     * <p>The spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}