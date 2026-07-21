package net.pottercraft.ollivanders2.spell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for spells that transfigure blocks into other material types — either a single default type or a
 * per-source-material mapping.
 *
 * @author Azami7
 * @see EntityTransfiguration
 * @see Transfiguration
 */
public abstract class BlockTransfiguration extends Transfiguration {
    /**
     * Lower limit for {@link #effectRadius}, in blocks.
     */
    int minEffectRadius = 1;

    /**
     * Upper limit for {@link #effectRadius}, in blocks.
     */
    int maxEffectRadius = 10;

    /**
     * Per-source-material target overrides. A block whose type is a key is changed to the mapped value; any other
     * block is changed to {@link #transfigureType}. Empty by default.
     */
    protected Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * The default material blocks are transfigured into when not overridden by {@link #transfigurationMap}.
     */
    protected Material transfigureType = Material.AIR;

    /**
     * The radius of blocks affected around the target, in blocks. Computed by {@link #setEffectRadius()}.
     */
    protected int effectRadius = 1;

    /**
     * Scales how much caster skill affects {@link #effectRadius}; see {@link #setEffectRadius()} for the formula.
     */
    protected double effectRadiusModifier = 1.0;

    /**
     * If true, this spell may transfigure pass-through blocks (AIR, water, fire); by default they are skipped. Used by
     * spells like AGUAMENTI that target water.
     */
    protected boolean effectsPassThrough = false;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public BlockTransfiguration(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public BlockTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;

        // material black list
        materialBlockedList.addAll(Ollivanders2Common.getUnbreakableMaterials());

        // required worldGuard state flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        minDuration = 15 * Ollivanders2Common.ticksPerSecond;
        maxDuration = 10 * Ollivanders2Common.ticksPerMinute;

        successMessage = "Transfiguration successful.";
        failureMessage = "Transfiguration failed.";
    }

    /**
     * Transfigure every eligible block within {@link #effectRadius} of the target. Kills the spell if no block could
     * be transfigured.
     */
    protected void transfigure() {
        if (isTransfigured || isKilled() || !hasHitBlock() || getTargetBlock() == null)
            return;

        // set the spell affect radius
        setEffectRadius();
        common.printDebugMessage("BlockTransfiguration.transfigure: effect radius is " + effectRadius, null, null, false);

        List<Block> blocksInRadius = BlockCommon.getBlocksInRadius(getTargetBlock().getLocation(), effectRadius); // we use getTargetBlock since spells like Aguamenti target the block above location

        // get the blocks to be transfigured
        for (Block blockToChange : blocksInRadius) {
            common.printDebugMessage("BlockTransfiguration.transfigure: checking block at " + blockToChange.getLocation().getX() + ", " + blockToChange.getLocation().getY() + ", " + blockToChange.getLocation().getZ(), null, null, false);
            if (!canTransfigure(blockToChange))
                continue;

            common.printDebugMessage("BlockTransfiguration.transfigure: transfiguring block", null, null, false);
            Material originalMaterial = blockToChange.getType();
            // if not permanent, keep track of what the original block was
            if (!permanent) {
                Ollivanders2API.getBlocks().addTemporarilyChangedBlock(blockToChange, this);
            }

            if (transfigurationMap.containsKey(originalMaterial))
                blockToChange.setType(transfigurationMap.get(originalMaterial));
            else
                blockToChange.setType(transfigureType);

            common.printDebugMessage("BlockTransfiguration.transfigure: new block type is " + blockToChange.getType(), null, null, false);
            isTransfigured = true;
        }

        if (!isTransfigured) {
            kill();
        }
    }

    /**
     * Set {@link #effectRadius} to {@code (usesModifier / 10) * effectRadiusModifier}, limited to
     * [{@link #minEffectRadius}, {@link #maxEffectRadius}].
     */
    protected void setEffectRadius() {
        effectRadius = (int) ((usesModifier / 10) * effectRadiusModifier);

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Check whether a block is an eligible target: a targetable material (satisfying the allow/blocked lists and, by
     * default, not a pass-through block) that is not already magically altered or already the target type. The result
     * is also gated by the spell's success rate, so it may return false at random even for an otherwise-valid block.
     *
     * @param block the block to validate
     * @return true if the block can be transfigured, false otherwise
     */
    boolean canTransfigure(@NotNull Block block) {
        common.printDebugMessage("BlockTransfigure.canTranfigure: Checking if this block can be transfigured.", null, null, false);

        // first check success rate
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand >= successRate) {
            common.printDebugMessage("BlockTransfigure.canTranfigure: " + caster.getName() + " failed success check in canTransfigure()", null, null, false);
            return false;
        }

        // get block type
        Material blockType = block.getType();

        if (Ollivanders2API.getBlocks().isTemporarilyChangedBlock(block)) {
            // do not change if this block is already magically altered, this must be checked first because below conditions may also be true
            common.printDebugMessage("BlockTransfigure.canTranfigure: Block is already magically altered", null, null, false);
            return false;
        }
        else if (projectilePassThrough.contains(blockType) && !effectsPassThrough) {
            // do not change pass-through blocks
            common.printDebugMessage("BlockTransfigure.canTranfigure: block is a pass-through block", null, null, false);
            return false;
        }
        else if (!materialAllowList.isEmpty() && !materialAllowList.contains(blockType)) {
            // do not change if the allowed list exists and this block is not in it
            common.printDebugMessage("BlockTransfigure.canTranfigure: Material not on allow list: " + blockType, null, null, false);
            return false;
        }
        else if (materialBlockedList.contains(blockType)) {
            // do not change if this block is in the blocked list
            common.printDebugMessage("BlockTransfigure.canTranfigure: Material on blocked list: " + blockType, null, null, false);
            return false;
        }
        else if (transfigurationMap.isEmpty() && blockType == transfigureType) {
            // do not change if this block is already the target type
            common.printDebugMessage("BlockTransfigure.canTranfigure: Block is already type " + transfigureType, null, null, false);
            return false;
        }

        common.printDebugMessage("BlockTransfigure.canTranfigure: block can be transfigured", null, null, false);

        return true;
    }

    /**
     * Restore all blocks this spell temporarily changed. No-op for permanent spells.
     */
    @Override
    public void revert() {
        if (permanent)
            return;

        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);

        // do any spell specific revert actions beyond changing the block back
        doRevert();
    }

    /**
     * Hook for spell-specific cleanup run at the end of {@link #revert()}. The default implementation does nothing.
     */
    void doRevert() {
    }

    /**
     * @param block the block to check
     * @return true if this spell is temporarily affecting the block; always false for permanent spells
     */
    @Override
    public boolean isTransfigured(@NotNull Block block) {
        if (permanent)
            // if this spell is permanent, it is no longer considered as "affecting" the block, the block *is* the new type
            // this condition should never happen because permanent spells should be killed but just in case one is hanging around
            return false;

        return Ollivanders2API.getBlocks().getBlocksChangedBySpell(this).contains(block);
    }

    /**
     * @param entity the entity to check
     * @return always false; block transfigurations do not affect entities (see {@link EntityTransfiguration})
     */
    @Override
    public boolean isTransfigured(@NotNull Entity entity) {
        return false;
    }

    /**
     * Get the per-source-material target overrides.
     *
     * @return a copy of the transfiguration map; when empty, all blocks change to {@link #transfigureType}
     */
    @NotNull
    public Map<Material, Material> getTransfigurationMap() {
        return new HashMap<>() {{
            putAll(transfigurationMap);
        }};
    }

    /**
     * Get the material type this spell transfigures blocks in to when a target material is not in {@link #transfigurationMap}.
     *
     * @return the transfiguration material
     */
    @NotNull
    public Material getTransfigureType() {
        return transfigureType;
    }

    /**
     * @return the effect radius in blocks, as set by {@link #setEffectRadius()}
     */
    public int getEffectRadius() {
        return effectRadius;
    }

    /**
     * @return the minimum effect radius in blocks
     */
    public int getMinRadius() {
        return minEffectRadius;
    }

    /**
     * @return the maximum effect radius in blocks
     */
    public int getMaxRadius() {
        return maxEffectRadius;
    }
}
