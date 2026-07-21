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
 * Colloportus (Locking Spell): a permanent stationary spell that stops doors, trapdoors, and chests within its radius
 * from being opened, broken, or otherwise changed. It cannot be dispelled by normal aging.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Locking_Spell">Harry Potter Wiki - Locking Spell</a>
 */
public class COLLOPORTUS extends O2StationarySpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 2;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 2;

    /**
     * Duration bound, unused because Colloportus is permanent.
     */
    public static final int minDurationConfig = 1000;

    /**
     * Duration bound, unused because Colloportus is permanent.
     */
    public static final int maxDurationConfig = 1000;


    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public COLLOPORTUS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.COLLOPORTUS;
        permanent = true;
    }

    /**
     * Constructor for casting a new Colloportus spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     */
    public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.COLLOPORTUS;

        permanent = true;

        radius = minRadius;
        duration = minDuration;

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * No-op; this spell is permanent and never ages.
     */
    @Override
    public void upkeep() {
    }

    /**
     * Cancel breaking a block inside the spell radius.
     *
     * @param event the event
     */
    @Override
    void doOnBlockBreakEvent(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled BlockBreakEvent", null, null, false);
        }
    }

    /**
     * Cancel an entity breaking a door inside the spell radius.
     *
     * @param event the event
     */
    @Override
    void doOnEntityBreakDoorEvent(@NotNull EntityBreakDoorEvent event) {
        Block block = event.getBlock();

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled EntityBreakDoorEvent", null, null, false);
        }
    }

    /**
     * Cancel an entity changing a block inside the spell radius.
     *
     * @param event the event
     */
    @Override
    void doOnEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
        Block block = event.getBlock();

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled EntityChangeBlockEvent", null, null, false);
        }
    }

    /**
     * Cancel an entity interacting with a block inside the spell radius.
     *
     * @param event the event
     */
    @Override
    void doOnEntityInteractEvent(@NotNull EntityInteractEvent event) {
        Block block = event.getBlock();

        if (isLocationInside(block.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("COLLOPORTUS: canceled EntityInteractEvent", null, null, false);
        }
    }

    /**
     * Cancel a player interacting with a block inside the spell radius.
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