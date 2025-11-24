package net.pottercraft.ollivanders2.spell;

import java.util.HashMap;
import java.util.Map;

import com.sk89q.worldguard.protection.flags.Flags;
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
    static int minRadius = 1;
    static int maxRadius = 10;

    /**
     * A map of the transfigured blocks and their original types for use with revert()
     */
    final HashMap<Block, Material> changedBlocks = new HashMap<>();

    /**
     * If this is populated, any material type key will be changed to the value
     */
    protected Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * The material type to change this block to.
     */
    Material transfigureType = Material.AIR;

    /**
     * How many blocks out from the target are affects.  Usually for permanent spells this is 1.
     */
    protected int radius = 1;

    /**
     * Allows spell variants to change the radius of the spell.
     */
    double radiusModifier = 1.0;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BlockTransfiguration(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public BlockTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;

        // material black list
        materialBlockedList.addAll(Ollivanders2Common.unbreakableMaterials);

        // required worldGuard state flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        minDuration = 15 * Ollivanders2Common.ticksPerSecond;
        maxDuration = 10 * Ollivanders2Common.ticksPerMinute;
        spellDuration = minDuration;

        successMessage = "Transfiguration successful.";
        failureMessage = "Transfiguration failed.";
    }

    /**
     * Transfigures blocks in a radius around the spell's target location.
     *
     * <p>This method is called each tick while the spell is active. It identifies all blocks within
     * the spell's radius from the target location and attempts to transfigure them. The radius is
     * modified by {@link #radiusModifier} and clamped between {@link #minRadius} and {@link #maxRadius}.</p>
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
        if (isTransfigured || !hasHitTarget() || getTargetBlock() == null)
            return;

        radius = radius * (int) radiusModifier;

        if (radius < minRadius)
            radius = minRadius;
        else if (radius > maxRadius)
            radius = maxRadius;

        // get the objects to be transfigured, target block is not always the block at the location (such as aguamenti), so we cannot use 'location'
        for (Block blockToChange : Ollivanders2Common.getBlocksInRadius(getTargetBlock().getLocation(), radius)) {
            if (!canTransfigure(blockToChange))
                continue;

            Material originalMaterial = blockToChange.getType();
            // if not permanent, keep track of what the original block was
            if (!permanent)
                changedBlocks.put(blockToChange, originalMaterial);

            if (transfigurationMap.containsKey(originalMaterial))
                blockToChange.setType(transfigurationMap.get(originalMaterial));
            else
                blockToChange.setType(transfigureType);

            isTransfigured = true;
        }

        if (isTransfigured)
            sendSuccessMessage();
        else {
            sendFailureMessage();
            kill();
        }

        if (permanent)
            kill();
    }

    /**
     * Determines if this block can be transfigured by this spell.
     *
     * <p>Performs comprehensive validation checks to ensure the block meets all requirements for
     * transfiguration. Checks are performed in the following order:
     * <ol>
     * <li><strong>Success Rate:</strong> Random chance based on player skill level (successRate)</li>
     * <li><strong>Target Type Check:</strong> Block is not already the target material type
     *     (checked via transfigurationMap if populated, or direct type comparison)</li>
     * <li><strong>Blocked List:</strong> Block material is not on the blocked list
     *     (e.g., unbreakable materials)</li>
     * <li><strong>Allow List:</strong> If an allow list is configured, block must be on it</li>
     * <li><strong>Existing Transfigurations:</strong> Block is not already transfigured by another
     *     active spell</li>
     * </ol></p>
     *
     * @param block the block to check
     * @return true if the block passes all validation checks and can be transfigured, false otherwise
     */
    boolean canTransfigure(@NotNull Block block) {
        common.printDebugMessage("BlockTransfigure.canTranfigure: Checking if this block can be transfigured.", null, null, false);

        // first check success rate
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand >= successRate) {
            common.printDebugMessage(player.getName() + " failed success check in canTransfigure()", null, null, false);
            return false;
        }

        // get block type
        Material blockType = block.getType();

        if (!transfigurationMap.isEmpty() && transfigurationMap.containsKey(transfigureType) && transfigurationMap.get(transfigureType) == blockType) {
            // do not change if this block is already the target type
            common.printDebugMessage("Block is already type " + transfigureType.toString(), null, null, false);
            return false;
        }
        else if (blockType == transfigureType) {
            // do not change if this block is already the target type
            common.printDebugMessage("Block is already type " + transfigureType.toString(), null, null, false);
            return false;
        }
        else if (materialBlockedList.contains(blockType)) {
            // do not change if this block is in the blocked list
            common.printDebugMessage("Material on blocked list: " + blockType.toString(), null, null, false);
            return false;
        }
        else if (!materialAllowList.isEmpty() && !materialAllowList.contains(blockType)) {
            // do not change if the allowed list exists and this block is not in it
            common.printDebugMessage("Material not on allow list: " + blockType.toString(), null, null, false);
            return false;
        }
        else {
            // do not change if this block is already the subject of a temporary transfiguration
            for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
                if (spell instanceof TransfigurationBase) {
                    if (((TransfigurationBase) spell).isBlockTransfigured(block)) {
                        common.printDebugMessage("Block is already a non-permanent transfiguration", null, null, false);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Restores all transfigured blocks to their original material types.
     *
     * <p>Called when the spell duration expires or the spell is explicitly ended. This method only
     * applies to temporary (non-permanent) transfigurations. Permanent transfigurations are not reverted
     * and the spell should be killed immediately after transfiguration.</p>
     *
     * <p>Iterates through all blocks stored in {@link #changedBlocks} and restores each block to its
     * original material type. After all blocks are reverted, clears the changedBlocks map and calls
     * {@link #doRevert()} for any subclass-specific cleanup.</p>
     */
    @Override
    public void revert() {
        if (permanent)
            return;

        for (Block block : changedBlocks.keySet()) {
            Material originalMaterial = changedBlocks.get(block);
            block.setType(originalMaterial);
        }

        changedBlocks.clear();

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

        return changedBlocks.containsKey(block);
    }

    /**
     * Is this entity transfigured by this spell
     *
     * @param entity the entity to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isEntityTransfigured(@NotNull Entity entity) {
        return false;
    }
}
