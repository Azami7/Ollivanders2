package net.pottercraft.ollivanders2.spell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.block.O2Blocks;
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
 * Abstract base class for block transfiguration spells.
 *
 * <p>Manages the transfiguration of blocks and terrain into other material types. Provides core
 * transfiguration logic including block targeting, type validation, success rate checking, and
 * reversion. Block transfigurations can be temporary (reverted after spell duration) or permanent.</p>
 *
 * <p>Subclasses must override this class to define specific block transformation behavior.
 * Configuration of {@link #transfigureType} (target material) and optionally {@link #transfigurationMap}
 * (mapping of source materials to different targets) should be done in subclass constructors.</p>
 *
 * <p>Affected blocks are validated against WorldGuard BUILD flag if enabled, and against material
 * block and allow lists defined in the configuration.</p>
 *
 * @author Azami7
 * @see EntityTransfiguration for entity transfiguration alternative
 * @see TransfigurationBase for base transfiguration mechanics
 */
public abstract class BlockTransfiguration extends TransfigurationBase {
    /**
     * Minimum effect radius (1 block by default).
     *
     * <p>The calculated radius is clamped to not go below this value.
     * Can be overridden by subclasses to set spell-specific minimums.</p>
     */
    int minEffectRadius = 1;

    /**
     * Maximum effect radius (10 blocks by default).
     *
     * <p>The calculated radius is clamped to not exceed this value.
     * Can be overridden by subclasses to set spell-specific maximums.</p>
     */
    int maxEffectRadius = 10;

    /**
     * Optional mapping of source materials to target materials.
     *
     * <p>If populated, blocks of types in this map's keys will be transfigured to their
     * mapped values. If a block type is not in this map, it will be changed to
     * {@link #transfigureType} instead. This allows spells to have different target
     * materials based on the source block type.</p>
     *
     * <p>Example: A spell could transfigure STONE → STONE_BRICKS and COBBLESTONE → STONE.</p>
     */
    protected Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * The default material type this spell transfigures blocks into.
     *
     * <p>Used when a block type is not in {@link #transfigurationMap}.
     * Subclasses should set this in their constructors to define the spell's
     * primary transfiguration target.</p>
     */
    protected Material transfigureType = Material.AIR;

    /**
     * The current effect radius for this spell in blocks.
     *
     * <p>Determines how many blocks out from the target location are affected by
     * the transfiguration. Calculated based on player skill and
     * then clamped between {@link #minEffectRadius} and {@link #maxEffectRadius}.</p>
     */
    protected int effectRadius = 1;

    /**
     * Multiplier on the effect radius calculation
     */
    protected double effectRadiusModifier = 1.0;

    /**
     * Whether this spell can transfigure pass-through blocks.
     *
     * <p>By default, blocks in {@link #projectilePassThrough} (AIR, CAVE_AIR, WATER, FIRE)
     * cannot be transfigured. Setting this to true allows pass-through blocks to be
     * transfigured. Used by spells like AGUAMENTI that need to modify water blocks.</p>
     */
    protected boolean effectsPassThrough = false;

    /**
     * Default constructor for spell initialization and documentation.
     *
     * <p>Used only for generating spell text in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public BlockTransfiguration(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * Constructor for casting block transfiguration spells.
     *
     * <p>Initializes spell with player context, wand information, and default block
     * transfiguration settings including:</p>
     * <ul>
     * <li>Unbreakable materials added to blocked list</li>
     * <li>WorldGuard BUILD flag requirement (if enabled)</li>
     * <li>Default duration bounds: 15 seconds to 10 minutes</li>
     * <li>Success and failure messages</li>
     * </ul>
     *
     * <p>Subclasses should call this constructor via super() and then configure spell-specific
     * properties like {@link #transfigureType} and {@link #transfigurationMap}.</p>
     *
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
     * Transfigures blocks in a radius around the spell's target location.
     *
     * <p>This method is called each tick while the spell is active. It identifies all blocks within
     * the spell's radius from the target location and attempts to transfigure them. The radius is
     * clamped between {@link #minEffectRadius} and {@link #maxEffectRadius}.</p>
     *
     * <p>For permanent spells, the spell is immediately killed after transfiguration. For temporary
     * spells, the spell continues until its duration expires.</p>
     *
     * <p>Validation checks are performed via {@link #canTransfigure(Block)} for each block:</p>
     * <ul>
     * <li>Success rate check must pass (based on player skill)</li>
     * <li>Block cannot already be the target material type</li>
     * <li>Block cannot be on the material blocked list (unbreakable materials)</li>
     * <li>Block must be on the allow list if one is populated</li>
     * <li>Block cannot already be transfigured by another active spell</li>
     * </ul>
     *
     * <p>If no blocks are successfully transfigured, the spell fails and is killed.</p>
     */
    protected void transfigure() {
        if (isTransfigured || isKilled() || !hasHitTarget() || getTargetBlock() == null)
            return;

        // set the spell affect radius
        setEffectRadius();
        common.printDebugMessage("BlockTransfiguration.transfigure: effect radius is " + effectRadius, null, null, false);

        List<Block> blocksInRadius = Ollivanders2Common.getBlocksInRadius(getTargetBlock().getLocation(), effectRadius); // we use getTargetBlock since spells like Aguamenti target the block above location

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
     * Calculates and sets the effect radius based on player skill level.
     *
     * <p>Called from {@link #transfigure()} on each tick to dynamically adjust the radius
     * based on the caster's spell skill. The radius is calculated as:
     * <code>radius = (usesModifier / 10) * effectRadiusModifier</code></p>
     *
     * <p>The calculated radius is clamped to [minEffectRadius, maxEffectRadius]:</p>
     * <ul>
     * <li>usesModifier = player's spell skill (0-100+ based on spell usage)</li>
     * <li>effectRadiusModifier = spell-specific scaling (e.g., 0.1 = 10% scale)</li>
     * <li>Result = number of blocks in radius around target</li>
     * </ul>
     *
     * <p>Example: With usesModifier=100 and effectRadiusModifier=1.0, radius = 10 blocks
     * (100/10 * 1.0 = 10), which affects all blocks within a 10-block sphere of the target.</p>
     */
    protected void setEffectRadius() {
        effectRadius = (int) ((usesModifier / 10) * effectRadiusModifier);

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Determines if a block can be transfigured by this spell.
     *
     * <p>Performs comprehensive validation checks to ensure the block meets all requirements for
     * transfiguration. Validation order is critical to avoid unnecessary checks:</p>
     * <ol>
     * <li><strong>Success Rate:</strong> Random chance based on player skill level (0-100%)</li>
     * <li><strong>Already Changed:</strong> Block is not already affected by another active
     *     transfiguration spell (checked first to avoid redundant checks)</li>
     * <li><strong>Pass-Through Check:</strong> Block is not a pass-through material (unless
     *     {@link #effectsPassThrough} is enabled)</li>
     * <li><strong>Target Type Check:</strong> Block is not already the target material type
     *     (checked via {@link #transfigurationMap} if populated, or direct comparison)</li>
     * <li><strong>Blocked List:</strong> Block material is not on {@link #materialBlockedList}
     *     (e.g., unbreakable materials)</li>
     * <li><strong>Allow List:</strong> If {@link #materialAllowList} is configured, block must be on it</li>
     * </ol>
     *
     * @param block the block to validate
     * @return true if the block passes all validation checks and can be transfigured, false otherwise
     */
    boolean canTransfigure(@NotNull Block block) {
        common.printDebugMessage("BlockTransfigure.canTranfigure: Checking if this block can be transfigured.", null, null, false);

        // first check success rate
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand >= successRate) {
            common.printDebugMessage("BlockTransfigure.canTranfigure: " + player.getName() + " failed success check in canTransfigure()", null, null, false);
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
     * Restores all transfigured blocks to their original material types.
     *
     * <p>Called when the spell duration expires or the spell is explicitly ended. This method only
     * applies to temporary (non-permanent) transfigurations. Permanent transfigurations are not reverted
     * and the spell should be killed immediately after transfiguration.</p>
     *
     * <p>Iterates through all blocks stored in {@link O2Blocks} and restores each block to its
     * original material type. After all blocks are reverted, clears the changedBlocks map and calls
     * {@link #doRevert()} for any subclass-specific cleanup.</p>
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
     * Performs spell-specific revert actions beyond block restoration.
     *
     * <p>Called at the end of the revert process after all blocks have been restored to their original
     * material types. Override this method in subclasses to perform any additional cleanup or restoration
     * logic specific to the spell (e.g., removing applied effects, adjusting neighbor blocks, etc.).</p>
     *
     * <p>Default implementation does nothing. Subclasses should override if needed.</p>
     */
    void doRevert() {
    }

    /**
     * Is this spell currently affecting this block?
     *
     * @param block the block to check
     * @return true if this spell is affecting this block, false otherwise
     */
    @Override
    public boolean isBlockTransfigured(@NotNull Block block) {
        if (permanent)
            // if this spell is permanent, it is no longer considered as "affecting" the block, the block *is* the new type
            // this condition should never happen because permanent spells should be killed but just in case one is hanging around
            return false;

        return Ollivanders2API.getBlocks().getBlocksChangedBySpell(this).contains(block);
    }

    /**
     * Check if an entity is transfigured by this spell.
     *
     * <p>Always returns false for block transfiguration spells, as they only affect blocks.
     * Entity transfigurations are handled by {@link EntityTransfiguration}.</p>
     *
     * @param entity the entity to check
     * @return false, as block transfiguration spells do not affect entities
     */
    @Override
    public boolean isEntityTransfigured(@NotNull Entity entity) {
        return false;
    }

    /**
     * Get the transfiguration material mapping for this spell.
     *
     * @return the transfiguration map, if empty, all materials change to {@link #transfigureType}
     */
    @NotNull
    public Map<Material, Material> getTransfigurationMap() {
        Map<Material, Material> map = new HashMap<>();
        map.putAll(transfigurationMap);

        return map;
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
     * Gets the current effect radius for this spell.
     *
     * <p>The radius determines how many blocks out from the target are affected by
     * the transfiguration. Returns the value calculated and clamped by {@link #setEffectRadius()}.</p>
     *
     * @return the effect radius in blocks
     */
    public int getEffectRadius() {
        return effectRadius;
    }

    /**
     * Gets the minimum allowed effect radius for this spell.
     *
     * @return the minimum radius in blocks
     */
    public int getMinRadius() {
        return minEffectRadius;
    }

    /**
     * Gets the maximum allowed effect radius for this spell.
     *
     * @return the maximum radius in blocks
     */
    public int getMaxRadius() {
        return maxEffectRadius;
    }
}
