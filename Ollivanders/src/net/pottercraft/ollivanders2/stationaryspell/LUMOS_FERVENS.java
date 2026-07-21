package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.block.BlockCommon;
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
 * Lumos Fervens (Bluebell Flames): a stationary spell that places waterproof soul fire that does not burn entities in
 * its radius. Cast by {@link net.pottercraft.ollivanders2.spell.LUMOS_FERVENS}.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bluebell_Flames">Harry Potter Wiki - Bluebell Flames</a>
 */
public class LUMOS_FERVENS extends O2StationarySpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 2;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 2;

    /**
     * Minimum spell duration: 5 minutes.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration: 2 hours.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerHour * 2;

    /**
     * The block changed to soul sand to support the fire; reverted on cleanup.
     */
    private Block baseBlock;

    /**
     * The material the base block is set to while the spell is active.
     */
    public static final Material baseBlockMaterial = Material.SOUL_SAND;

    /**
     * The base block's material before the spell changed it, restored on cleanup.
     */
    private Material originalMaterial;

    /**
     * The block that holds the soul fire, directly above {@link #baseBlock}.
     */
    private Block fireBlock;

    /**
     * The material placed for the flames while the spell is active.
     */
    public static final Material fireBlockMaterial = Material.SOUL_FIRE;

    /**
     * Serialization key for {@link #originalMaterial}.
     */
    private static final String originalMaterialLabel = "original_material";

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.LUMOS_FERVENS;
    }

    /**
     * Constructor for casting a new Lumos Fervens spell. Places the soul fire immediately.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.LUMOS_FERVENS;

        setRadius(radius);
        setDuration(duration, false);

        createSoulFire();
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Get the base block the fire sits on.
     *
     * @return the base block
     */
    public Block getBaseBlock() {
        return baseBlock;
    }

    /**
     * Get the block holding the soul fire.
     *
     * @return the fire block
     */
    public Block getFireBlock() {
        return fireBlock;
    }

    /**
     * Age the spell and, every 10 ticks, rebuild the soul fire if it is missing (e.g. after a server restart).
     */
    @Override
    public void upkeep() {
        if (isKilled())
            return;

        if (duration % 10 == 0) {
            if (fireBlock.getType() != fireBlockMaterial) {
                createSoulFire();
            }
        }
        age();
    }

    /**
     * Place the soul sand base and soul fire above it. Kills the spell if the block above the location is not air.
     */
    private void createSoulFire() {
        baseBlock = location.getBlock();
        originalMaterial = baseBlock.getType();
        fireBlock = baseBlock.getRelative(BlockFace.UP);

        if (fireBlock.getType() == Material.AIR) {
            baseBlock.setType(baseBlockMaterial);
            fireBlock.setType(fireBlockMaterial);
        }
        else {
            kill();
        }
    }

    /**
     * Cancel fire damage to an entity inside the spell radius.
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
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                    event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancel combustion of an entity inside the spell radius.
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
            event.setCancelled(true);
        }
    }

    /**
     * Stop flowing water from reaching the fire block, keeping the waterproof flames lit.
     *
     * @param event the event
     */
    @Override
    void doOnBlockFromToEvent(@NotNull BlockFromToEvent event) {
        if (!active || fireBlock == null) {
            return;
        }

        Material fromType = event.getBlock().getType();
        if (fromType == Material.WATER) {
            if (event.getToBlock().equals(fireBlock)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Stop a player emptying a bucket onto or next to the fire block.
     *
     * @param event the event
     */
    @Override
    void doOnPlayerBucketEmptyEvent(@NotNull PlayerBucketEmptyEvent event) {
        if (!active || fireBlock == null) {
            return;
        }

        Block targetBlock = event.getBlock();

        if (targetBlock.equals(fireBlock) || BlockCommon.isAdjacentTo(targetBlock, fireBlock)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("The bluebell flames resist your attempt to extinguish them with water!");
        }
    }

    /**
     * Kill the spell if its fire block or base block is broken.
     *
     * @param event the event
     */
    @Override
    void doOnBlockBreakEvent(@NotNull BlockBreakEvent event) {
        if (event.getBlock().equals(baseBlock) || event.getBlock().equals(fireBlock)) {
            kill();
        }
    }

    /**
     * Stop an entity (enderman, falling block, etc.) changing the fire block or base block.
     *
     * @param event the event
     */
    @Override
    void doOnEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
        if (!active) {
            return;
        }

        if (event.getBlock().equals(baseBlock) || event.getBlock().equals(fireBlock)) {
            event.setCancelled(true);
        }
    }

    /**
     * @return the serialized spell data, holding the base block's original material
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> spellData = new HashMap<>();

        if (originalMaterial != null) {
            spellData.put(originalMaterialLabel, originalMaterial.toString());
        }

        return spellData;
    }

    /**
     * Restore the base and fire block references and the base block's original material from saved data.
     *
     * @param spellData the serialized spell data
     */
    @Override
    void deserializeSpellData(@NotNull Map<String, String> spellData) {
        baseBlock = location.getBlock();
        fireBlock = baseBlock.getRelative(BlockFace.UP);

        if (spellData.containsKey(originalMaterialLabel)) {
            originalMaterial = Material.getMaterial(spellData.get(originalMaterialLabel));
        }
    }

    /**
     * Remove the soul fire and restore the base block to its original material.
     */
    @Override
    void doCleanUp() {
        if (fireBlock != null) {
            fireBlock.setType(Material.AIR);
        }

        if (baseBlock != null && originalMaterial != null) {
            baseBlock.setType(originalMaterial);
        }
    }

    /**
     * @return true if the caster UUID, location, and base block's original material are all present
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && originalMaterial != null;
    }
}