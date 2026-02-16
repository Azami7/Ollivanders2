package net.pottercraft.ollivanders2.stationaryspell;

import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * A stationary locking spell that prevents access to doors, trapdoors, and chests.
 *
 * <p>The Colloportus charm (Locking Spell) creates a protective barrier that prevents entities from
 * opening, breaking, or interacting with doors, trapdoors, and chests within the spell's protected area.
 * The spell is permanent and cannot be dispelled by normal means. Any attempt to open or break a protected
 * door or trapdoor will fail silently.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 *   <li>Radius: 5 blocks (fixed)</li>
 *   <li>Duration: Permanent (cannot be dispelled)</li>
 *   <li>Effect: Prevents opening/breaking doors, trapdoors, and chests within the protected area</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Locking_Spell">https://harrypotter.fandom.com/wiki/Locking_Spell</a>
 * @since 2.21
 */
public class COLLOPORTUS extends O2StationarySpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 2;

    /**
     * Maximum spell radius (5 blocks).
     */
    public static final int maxRadiusConfig = 2;

    /**
     * Minimum spell duration (not used - colloportus is permanent).
     */
    public static final int minDurationConfig = 1000;

    /**
     * Maximum spell duration (not used - colloportus is permanent).
     */
    public static final int maxDurationConfig = 1000;


    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public COLLOPORTUS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.COLLOPORTUS;
        permanent = true;
    }

    /**
     * Constructs a new COLLOPORTUS spell cast by a player.
     *
     * <p>Creates a colloportus charm at the specified location. The spell prevents opening and breaking
     * of doors, trapdoors, and chests within a 5-block radius.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     */
    public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.COLLOPORTUS;

        permanent = true;

        radius = minRadius;
        duration = minDuration;

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius to 5 blocks (fixed - not configurable) and duration constraints
     * which are not used since colloportus is permanent.</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig; // not used, colloportus is permanent
        maxDuration = maxDurationConfig; // not used, colloportus is permanent
    }

    /**
     * No upkeep for this spell
     */
    @Override
    public void upkeep() {
    }

    /**
     * Prevent doors and trapdoors being broken
     *
     * @param event the event
     */
    @Override
    void doOnBlockBreakEvent(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock(); // will never be null

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled BlockBreakEvent", null, null, false);
        }
    }

    /**
     * Prevent doors from being broken
     *
     * @param event the event
     */
    @Override
    void doOnEntityBreakDoorEvent(@NotNull EntityBreakDoorEvent event) {
        Block block = event.getBlock(); // will never be null

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled EntityBreakDoorEvent", null, null, false);
        }
    }

    /**
     * Prevent door and trapdoor blocks from being changed
     *
     * @param event the event
     */
    @Override
    void doOnEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
        Block block = event.getBlock(); // will never be null

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled EntityChangeBlockEvent", null, null, false);
        }
    }

    /**
     * Prevent doors and trapdoors from being interacted with
     *
     * @param event the event
     */
    @Override
    void doOnEntityInteractEvent(@NotNull EntityInteractEvent event) {
        Block block = event.getBlock(); // will never be null

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled EntityInteractEvent", null, null, false);
        }
    }

    /**
     * Prevent doors and trapdoors from being interacted with
     *
     * @param event the event
     */
    @Override
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled PlayerInteractEvent", null, null, false);
        }
    }

    /**
     * Serializes the colloportus spell data for persistence.
     *
     * <p>The colloportus spell has no extra data to serialize beyond the base spell properties,
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
     * Deserializes colloportus spell data from saved state.
     *
     * <p>The colloportus spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the colloportus spell ends.
     *
     * <p>The colloportus spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}