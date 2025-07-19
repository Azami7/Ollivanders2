package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockFromToEvent;
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
     * Min radius for this spell
     */
    public static final int minRadiusConfig = 1;
    /**
     * Max radius for this spell
     */
    public static final int maxRadiusConfig = 5;
    /**
     * Min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;
    /**
     * Max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerHour * 2;

    /**
     * The block that was changed to soul sand
     */
    private Block baseBlock;
    
    /**
     * The original material of the base block
     */
    private Material originalMaterial;
    
    /**
     * The block where the soul fire is placed
     */
    private Block fireBlock;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.LUMOS_FERVENS;
        
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
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
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.LUMOS_FERVENS;
        
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        
        setRadius(radius);
        setDuration(duration);
        
        // Create the initial soul fire
        createSoulFire();
    }

    /**
     * Create soul fire at the target location
     */
    private void createSoulFire() {
        Block targetBlock = location.getBlock();
        Block blockAbove = targetBlock.getRelative(BlockFace.UP);
        
        // Check if the block above is air so we can place fire
        if (blockAbove.getType() == Material.AIR) {
            // Change the target block to soul sand to support soul fire
            Material material = targetBlock.getType();
            if (material != Material.SOUL_SAND && material != Material.SOUL_SOIL) {
                baseBlock = targetBlock;
                originalMaterial = material;
                targetBlock.setType(Material.SOUL_SAND);
            }
            
            // Set the block above to soul fire
            blockAbove.setType(Material.SOUL_FIRE);
            fireBlock = blockAbove;
        } else {
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

    // Label for serialization
    private final String originalMaterialLabel = "original_material";
    
    /**
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @NotNull
    @Override
    Map<String, String> serializeSpellData() {
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
     * This is the stationary spell's effect. age() must be called in this if you want the spell to age and die eventually.
     */
    @Override
    void checkEffect() {
        age();
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
}