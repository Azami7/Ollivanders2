package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bluebell Flames produces harmless blue flames that are waterproof. These flames can be touched, penetrated, and held
 * without burning the holder, though they can singe materials such as clothing and plants.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_FERVENS}
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bluebell_Flames">https://harrypotter.fandom.com/wiki/Bluebell_Flames</a>
 * @since 2.21.4
 */
public class LUMOS_FERVENS extends O2StationarySpell {
    /**
     * Minimum spell radius (2 blocks).
     */
    public static final int minRadiusConfig = 2;

    /**
     * Maximum spell radius (2 blocks).
     */
    public static final int maxRadiusConfig = 2;

    /**
     * Minimum spell duration (5 minutes).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration (2 hours).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerHour * 2;

    /**
     * The block that was changed to soul sand
     */
    private Block baseBlock;

    /**
     * The material for the base block when spell is active
     */
    public static final Material baseBlockMaterial = Material.SOUL_SAND;

    /**
     * The original material of the base block
     */
    private Material originalMaterial;

    /**
     * The block where the soul fire is placed
     */
    private Block fireBlock;

    /**
     * The material for the fire when spell is active
     */
    public static final Material fireBlockMaterial = Material.SOUL_FIRE;

    /**
     * Label for serialization
     */
    private static final String originalMaterialLabel = "original_material";

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.LUMOS_FERVENS;
    }

    /**
     * Constructs a new LUMOS_FERVENS spell cast by a player.
     *
     * <p>Creates bluebell flames (soul fire) at the specified location with the given radius and duration.
     * The flames are waterproof and prevent fire damage to entities within the protected area.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.LUMOS_FERVENS;

        setRadius(radius);
        setDuration(duration);

        // Create the initial soul fire
        createSoulFire();
    }

    /**
     * Set the min/max values for radius and duration.
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Get the base block for this spell
     *
     * @return the base block
     */
    public Block getBaseBlock() {
        return baseBlock;
    }

    /**
     * Get the fire block for this spell
     *
     * @return the fire block
     */
    public Block getFireBlock() {
        return fireBlock;
    }

    /**
     * Start the flames if they have not been and age the spell each tick
     */
    @Override
    public void upkeep() {
        if (isKilled())
            return;

        if (duration % 10 == 0) { // check every 10 ticks
            if (fireBlock.getType() != fireBlockMaterial) { // we have not run createSoulFire()
                createSoulFire();
            }
        }
        age();
    }

    /**
     * Create soul fire at the target location
     */
    private void createSoulFire() {
        baseBlock = location.getBlock();
        originalMaterial = baseBlock.getType();
        fireBlock = baseBlock.getRelative(BlockFace.UP);

        // Check if the block above is air so we can place fire
        if (fireBlock.getType() == Material.AIR) {
            // Change the target block to soul sand to support soul fire
            baseBlock.setType(baseBlockMaterial);

            // Set the block above to soul fire
            fireBlock.setType(fireBlockMaterial);
        }
        else {
            // If we can't place fire, kill the spell
            kill();
        }
    }

    /**
     * Handle entity damage events - prevent fire damage within the spell radius
     *
     * @param event the event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        if (!active) {
            return;
        }

        Entity entity = event.getEntity();
        if (isLocationInside(entity.getLocation())) {
            // Prevent fire damage within the spell radius
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                    event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle entity combust events - prevent entities from burning within the spell radius
     *
     * @param event the event
     */
    @Override
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
        if (!active) {
            return;
        }

        Entity entity = event.getEntity();
        if (isLocationInside(entity.getLocation())) {
            // Cancel combustion for entities within the spell radius
            event.setCancelled(true);
        }
    }

    /**
     * Handle block from to events - prevent water from extinguishing the bluebell flames
     *
     * @param event the event
     */
    @Override
    void doOnBlockFromToEvent(@NotNull BlockFromToEvent event) {
        if (!active || fireBlock == null) {
            return;
        }

        // Check if this is water flowing
        Material fromType = event.getBlock().getType();
        if (fromType == Material.WATER) {
            // If water is flowing to our fire block, cancel the event
            if (event.getToBlock().equals(fireBlock)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle bucket empty events - prevent players from placing water on or near the bluebell flames
     *
     * @param event the event
     */
    @Override
    void doOnPlayerBucketEmptyEvent(@NotNull PlayerBucketEmptyEvent event) {
        if (!active || fireBlock == null) {
            return;
        }

        // Get the block where water would be placed
        Block targetBlock = event.getBlock();

        // Check if the target block is at or adjacent to the fire block
        if (targetBlock.equals(fireBlock) || Ollivanders2Common.isAdjacentTo(targetBlock, fireBlock)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("The bluebell flames resist your attempt to extinguish them with water!");
        }
    }

    /**
     * Handle block break events - kill the spell if the fire or base block is broken.
     *
     * <p>When a player attempts to break the soul fire block or the base block that supports it,
     * this spell is immediately terminated and cleaned up.</p>
     *
     * @param event the block break event (not null)
     */
    @Override
    void doOnBlockBreakEvent(@NotNull BlockBreakEvent event) {
        // kill this spell if the fire block or base block is broken
        if (event.getBlock().equals(baseBlock) || event.getBlock().equals(fireBlock)) {
            kill();
        }
    }

    /**
     * Handle entity change block events - prevent entities from changing the fire or base block.
     *
     * <p>When entities (such as endermen or falling sand) attempt to change the soul fire block
     * or the base block that supports it, this event is cancelled to protect the spell structure.</p>
     *
     * @param event the entity change block event (not null)
     */
    @Override
    void doOnEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
        if (!active) {
            return;
        }

        // cancel the event if the fire block or base block get changed
        if (event.getBlock().equals(baseBlock) || event.getBlock().equals(fireBlock)) {
            event.setCancelled(true);
        }
    }

    /**
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> spellData = new HashMap<>();

        // Save original material
        if (originalMaterial != null) {
            spellData.put(originalMaterialLabel, originalMaterial.toString());
        }

        return spellData;
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData the serialized spell data
     */
    @Override
    void deserializeSpellData(@NotNull Map<String, String> spellData) {
        // Set the base block to the location block
        baseBlock = location.getBlock();

        // Set up the fire block
        fireBlock = baseBlock.getRelative(BlockFace.UP);

        // Get the original material
        if (spellData.containsKey(originalMaterialLabel)) {
            originalMaterial = Material.getMaterial(spellData.get(originalMaterialLabel));
        }
    }

    /**
     * Clean up needed for this spell when it ends.
     */
    @Override
    void doCleanUp() {
        // Remove the soul fire block
        if (fireBlock != null) {
            fireBlock.setType(Material.AIR);
        }

        // Restore original block that was changed to soul sand
        if (baseBlock != null && originalMaterial != null) {
            baseBlock.setType(originalMaterial);
        }
    }

    /**
     * Checks if the spell was properly deserialized and has all required data.
     *
     * <p>Verifies that the spell's player UUID, location, and original material have been properly
     * restored from serialized data during server startup.</p>
     *
     * @return true if the spell has all required deserialized data, false otherwise
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && originalMaterial != null;
    }
}