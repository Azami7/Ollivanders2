package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.FULL_IMMOBILIZE;
import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for spells that immobilize a target player for a skill-scaled duration, optionally imprisoning them in
 * blocks. Subclasses implement {@link #canTarget(Player)} and may override {@link #addAdditionalEffects(Player)}.
 *
 * @author Azami7
 * @see IMMOBILIZE
 * @see FULL_IMMOBILIZE
 */
abstract public class ImmobilizePlayer extends O2Spell {
    /**
     * Lower limit for {@link #effectDuration}, in ticks.
     */
    int minEffectDuration = 30 * Ollivanders2Common.ticksPerSecond;

    /**
     * Upper limit for {@link #effectDuration}, in ticks.
     */
    int maxEffectDuration = 300 * Ollivanders2Common.ticksPerSecond;

    /**
     * The immobilization duration in ticks. Set by {@link #calculateDuration()}.
     */
    int effectDuration = 0;

    /**
     * Does this spell fully immobilize the target - location plus pitch and yaw. If false, it
     * will allow pitch and yaw changes.
     */
    boolean fullImmobilize = false;

    /**
     * If true, surrounds the target player with prison blocks when the spell hits.
     */
    boolean imprison = false;

    /**
     * The material used to create the prison blocks around the target player.
     */
    Material imprisonMaterial = Material.AIR;

    /**
     * If true, only the outer shell of the expanded bounding box is filled with prison blocks,
     * leaving the blocks the player physically occupies unchanged to prevent suffocation damage.
     */
    boolean prisonIsShell = false;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ImmobilizePlayer(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ImmobilizePlayer(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Does this spell fully immobilize - location/velocity and pitch/yaw - or not.
     *
     * @return true if the spell does full immobilize, false if it only affects location/velocity
     */
    public boolean isFullImmobilize() {
        return fullImmobilize;
    }

    /**
     * Immobilize the first valid nearby player (excluding the caster), applying any additional effects and
     * imprisoning them when configured, then end the spell. Killed if the projectile hits a block first.
     */
    @Override
    protected void doCheckEffect() {
        // projectile has stopped, kill the spell
        if (hasHitBlock())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(caster.getUniqueId()) || !canTarget(target)) {
                common.printDebugMessage("ImmobilizePlayerSuper.doCheckEffect: " + target.getName() + " cannot be targeting, skipping", null, null, false);
                continue;
            }

            common.printDebugMessage("ImmobilizePlayerSuper.doCheckEffect: targeting " + target.getName(), null, null, false);

            calculateDuration();
            addImmobilizationEffect(target);
            addAdditionalEffects(target);

            if (imprison)
                imprisonPlayer(target);

            kill();
            return;
        }
    }

    /**
     * Check whether a player can be targeted by this spell.
     *
     * @param target the player to validate as a potential target
     * @return true if the player can be immobilized, false otherwise
     */
    abstract boolean canTarget(Player target);

    /**
     * Set {@link #effectDuration} to {@code (usesModifier + 30) * ticksPerSecond}, limited to
     * [{@link #minEffectDuration}, {@link #maxEffectDuration}].
     */
    void calculateDuration() {
        effectDuration = ((int) usesModifier + 30) * Ollivanders2Common.ticksPerSecond;

        if (effectDuration > maxEffectDuration)
            effectDuration = maxEffectDuration;
        else if (effectDuration < minEffectDuration)
            effectDuration = minEffectDuration;
    }

    /**
     * Apply the immobilization effect to the target for {@link #effectDuration}, full or partial per
     * {@link #fullImmobilize}.
     *
     * @param target the player to immobilize
     */
    void addImmobilizationEffect(Player target) {
        IMMOBILIZE immobilize;

        if (fullImmobilize)
            immobilize = new FULL_IMMOBILIZE(p, effectDuration, false, target.getUniqueId());
        else
            immobilize = new IMMOBILIZE(p, effectDuration, false, target.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(immobilize);
    }

    /**
     * Enclose the target in {@link #imprisonMaterial}, replacing only the air blocks in the region around them (and
     * leaving the blocks they occupy clear when {@link #prisonIsShell} is set, to avoid suffocation). The blocks are
     * reverted automatically when the effect expires.
     *
     * @param target the player to surround with prison blocks
     */
    void imprisonPlayer(Player target) {
        BlockData additionalBlockData = getAdditionalPrisonBlockData();
        BoundingBox playerBoundingBox = null;
        if (prisonIsShell) {
            playerBoundingBox = target.getBoundingBox();
            if (playerBoundingBox == null) {
                common.printDebugMessage("ImmobilizePlayer.imprisonPlayer: playerBoundingBox is null", null, null, true);
                return;
            }
        }

        List<Block> blocks = calculateBlocksToChange(target.getBoundingBox().expand(1.0));
        for (Block block : blocks) {
            if (prisonIsShell) {
                BoundingBox blockBox = new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1, block.getZ() + 1);
                if (playerBoundingBox.overlaps(blockBox))
                    continue;
            }

            if (BlockCommon.isAirBlock(block)) {
                Ollivanders2API.getBlocks().addTemporarilyChangedBlock(block, this);
                block.setType(imprisonMaterial);
                if (additionalBlockData != null)
                    block.setBlockData(additionalBlockData);
            }
        }

        // clean up the blocks
        new BukkitRunnable() {
            @Override
            public void run() {
                revertBlocks();
            }
        }.runTaskLater(p, effectDuration);
    }

    /**
     * Hook for subclasses to supply extra block data applied to each prison block after its material is set. The
     * default implementation returns null.
     *
     * @return block data to apply, or null for none
     */
    @Nullable
    BlockData getAdditionalPrisonBlockData() {
        return null;
    }

    /**
     * Hook for subclasses to apply extra effects to the immobilized target. The default implementation does nothing.
     *
     * @param target the immobilized player
     */
    void addAdditionalEffects(Player target) {
    }

    /**
     * @param boundingBox the region to collect blocks from
     * @return every distinct block whose coordinates fall within the bounding box
     */
    List<Block> calculateBlocksToChange(BoundingBox boundingBox) {
        ArrayList<Block> blocks = new ArrayList<>();

        int minX = (int) Math.floor(boundingBox.getMinX());
        int minY = (int) Math.floor(boundingBox.getMinY());
        int minZ = (int) Math.floor(boundingBox.getMinZ());
        int maxX = (int) Math.floor(boundingBox.getMaxX());
        int maxY = (int) Math.floor(boundingBox.getMaxY());
        int maxZ = (int) Math.floor(boundingBox.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (!blocks.contains(block))
                        blocks.add(block);
                }
            }
        }

        return blocks;
    }

    /**
     * Get the calculated immobilization duration in game ticks.
     *
     * @return the effect duration in ticks
     */
    public int getEffectDuration() {
        return effectDuration;
    }

    /**
     * @return the minimum immobilization duration in ticks
     */
    public int getMinEffectDuration() {
        return minEffectDuration;
    }

    /**
     * @return the maximum immobilization duration in ticks
     */
    public int getMaxEffectDuration() {
        return maxEffectDuration;
    }

    /**
     * Whether this spell surrounds the target with prison blocks.
     *
     * @return true if the spell creates a prison around the target, false otherwise
     */
    public boolean doesImprison() {
        return imprison;
    }

    /**
     * Get the material used to build the prison around the target player.
     *
     * @return the prison block material
     */
    public Material getImprisonMaterial() {
        return imprisonMaterial;
    }

    /**
     * Whether this spell builds only the outer shell of the prison, leaving the player's
     * occupied blocks unchanged to prevent suffocation damage.
     *
     * @return true if only the shell is built, false if the entire expanded region is filled
     */
    public boolean isPrisonShell() {
        return prisonIsShell;
    }

    /**
     * Revert all prison blocks this spell placed. Runs automatically when the effect expires.
     */
    void revertBlocks() {
        if (imprison)
            Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }
}
