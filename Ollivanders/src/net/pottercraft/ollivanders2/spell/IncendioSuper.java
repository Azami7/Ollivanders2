package net.pottercraft.ollivanders2.spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for fire-setting spells that ignite blocks and entities.
 *
 * <p>IncendioSuper provides the core mechanics for fire-based offensive spells. It handles applying
 * fire effects to blocks, living entities, and items in a configurable area of effect. The spell's
 * behavior depends on several configuration flags: whether it strafes (affects multiple targets)
 * or single-targets, and whether it's currently burning.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Effect:</strong> Sets fire to blocks or living entities for a duration based on spell level</li>
 * <li><strong>Block Effects:</strong> Places temporary fire blocks above solid surfaces</li>
 * <li><strong>Entity Effects:</strong> Sets living entities and items on fire for calculated burn duration</li>
 * <li><strong>Duration:</strong> Calculated from usesModifier and durationModifier; clamped to 5-600 ticks</li>
 * <li><strong>Single-target vs Strafe:</strong> Configurable to affect single target or multiple targets in radius</li>
 * </ul>
 *
 * <p>Fire blocks are tracked as temporary changes via {@link Ollivanders2API#getBlocks()} to allow proper
 * reversion when the spell ends. Fire placed on netherrack or soul sand will burn permanently unless
 * explicitly reverted.</p>
 *
 * <p>Subclasses override this class to implement specific fire spells (e.g., INCENDIO, INCENDIO_DUO)
 * with different radius, duration, and strafe behaviors.</p>
 *
 * @author Azami7
 */
public abstract class IncendioSuper extends O2Spell {
    /**
     * The time remaining for the burn effect, in ticks.
     *
     * <p>Calculated from the caster's spell level (usesModifier) and the durationModifier.
     * This value is clamped between minBurnDuration and maxBurnDuration.</p>
     */
    private int burnDuration = 0;

    /**
     * Whether this spell affects multiple targets in a radius (true) or a single target (false).
     *
     * <p>When true, the spell will affect all entities and blocks within the configured radius.
     * When false, the spell will only affect the first entity or the target block above the hit location.</p>
     */
    boolean strafe = false;

    /**
     * Whether the spell is currently applying the burn effect to targets.
     *
     * <p>Set to true after the first effect application. On subsequent ticks, the spell checks if the
     * burn duration has elapsed and kills itself if so.</p>
     */
    boolean burning = false;

    /**
     * The radius in blocks for affecting living entities and items.
     *
     * <p>Used to determine which entities and items within this radius should be set on fire.
     * Typically ranges from 1 to 10 depending on the specific fire spell implementation.</p>
     */
    int entityRadius = 1;

    /**
     * The radius in blocks for affecting block transfiguration during strafe mode.
     *
     * <p>Only used when strafe is true. Determines which blocks should be set on fire in the area
     * around the target location. Single-target mode only affects the block directly above the target.</p>
     */
    int blockRadius = 1;

    /**
     * A modifier applied to the burn duration calculation to adjust duration based on spell level.
     *
     * <p>The final burn duration is calculated as:
     * <code>burnDuration = (usesModifier * durationModifier * ticksPerSecond)</code>.
     * Typical values range from 0.5 to 2.0, allowing subclasses to scale duration differently.</p>
     */
    int durationModifier = 1;

    /**
     * The maximum duration for the burning effect in ticks.
     *
     * <p>Subclasses may override this value to adjust the upper bound for burn duration.
     * Default is 30 seconds (600 ticks).</p>
     */
    protected int maxBurnDuration = 30 * Ollivanders2Common.ticksPerSecond;

    /**
     * The minimum duration for the burning effect in ticks.
     *
     * <p>Subclasses may override this value to adjust the lower bound for burn duration.
     * Default is 0.25 seconds (5 ticks).</p>
     */
    protected int minBurnDuration = Ollivanders2Common.ticksPerSecond / 4;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public IncendioSuper(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public IncendioSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // spell cannot pass through water or fire
        projectilePassThrough.removeAll(Ollivanders2Common.getHotBlocks());
        projectilePassThrough.remove(Material.WATER);

        // spell cannot target hot blocks
        materialBlockedList.addAll(Ollivanders2Common.getHotBlocks());

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.BUILD);
            worldGuardFlags.add(Flags.LIGHTER);
            worldGuardFlags.add(Flags.PVP);
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
        }
    }

    /**
     * Applies or maintains the fire effect on targets each tick.
     *
     * <p>On the first call (when not yet burning), this method calculates the burn duration, then sets
     * fire to the target block or blocks within the effect radius (depending on strafe mode), and applies
     * fire ticks to nearby living entities and items. It then sets {@link #burning} to true to indicate
     * the spell is now maintaining the burn effect.</p>
     *
     * <p>On subsequent ticks (when already burning), this method checks if the spell's age has exceeded
     * the calculated burn duration. If so, the spell is killed, allowing the {@link #revert()} method to
     * clean up any temporarily changed blocks.</p>
     *
     * <p>The spell requires a valid target block ({@link #hasHitTarget()}) to function. If the projectile
     * has not hit a target or the target block is null, the spell is killed immediately.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        if (burning) {
            if (getAge() > burnDuration)
                kill();
        }
        else {
            Block target = getTargetBlock();
            if (target == null) {
                common.printDebugMessage("IncendioSuper.doCheckEffect: target block is null", null, null, true);
                kill();
                return;
            }

            // set the burn duration
            if (burnDuration == 0)
                setBurnDuration();

            // blocks
            if (!strafe) {
                setBlockOnFire(target);
            }
            else {
                for (Block block : Ollivanders2Common.getBlocksInRadius(target.getLocation(), blockRadius))
                    setBlockOnFire(block);
            }

            // items, we have to check the block above the target because items would be sitting on top of the target
            List<Item> items = EntityCommon.getItemsInRadius(target.getRelative(BlockFace.UP).getLocation(), entityRadius);
            for (Item item : items) {
                common.printDebugMessage("IncendioSuper.doCheckEffect: found item " + item.getItemStack().getType(), null, null, false);
                item.setFireTicks(burnDuration);

                if (!strafe)
                    break;
            }

            // entities, we have to check the block above the target because entities would be standing on top of the target
            List<LivingEntity> livingEntities = EntityCommon.getLivingEntitiesInRadius(target.getRelative(BlockFace.UP).getLocation(), entityRadius);
            for (LivingEntity livingEntity : livingEntities) {
                common.printDebugMessage("IncendioSuper.doCheckEffect: found entity " + livingEntity.getType(), null, null, false);
                livingEntity.setFireTicks(burnDuration);

                if (!strafe)
                    break;
            }

            burning = true;
        }
    }

    /**
     * Get the burn duration for this spell.
     *
     * @return the burn duration, in ticks
     */
    public int getBurnDuration() {
        return burnDuration;
    }

    /**
     * Get the maximum burn duration for this spell.
     *
     * @return the maximum burn duration, in ticks
     */
    public int getMaxBurnDuration() {
        return maxBurnDuration;
    }

    /**
     * Get the minimum burn duration for this spell.
     *
     * @return the minimum burn duration, in ticks
     */
    public int getMinBurnDuration() {
        return minBurnDuration;
    }

    /**
     * Is this a strafe spell?
     *
     * @return true if it is a strafe spell, false otherwise
     */
    public boolean isStrafe() {
        return strafe;
    }

    /**
     * Calculates and sets the burn duration based on the caster's spell level.
     *
     * <p>The burn duration is calculated from the caster's spell proficiency (usesModifier) and the
     * spell-specific durationModifier: <code>burnDuration = usesModifier * durationModifier * ticksPerSecond</code>.
     * The result is then clamped to the range [minBurnDuration, maxBurnDuration] (approximately 5-600 ticks
     * or 0.25 seconds to 30 seconds).</p>
     *
     * <p>This method is called once per spell cast before applying fire effects, ensuring that all entities
     * and blocks affected by this spell will burn for the same calculated duration.</p>
     */
    void setBurnDuration() {
        burnDuration = (int) (usesModifier * durationModifier * Ollivanders2Common.ticksPerSecond);
        if (burnDuration > maxBurnDuration)
            burnDuration = maxBurnDuration;
        else if (burnDuration < minBurnDuration)
            burnDuration = minBurnDuration;
    }

    /**
     * Get the block radius for this spell's area of effect.
     *
     * @return the block radius in blocks
     */
    public int getBlockRadius() {
        return blockRadius;
    }

    /**
     * Sets a block on fire by placing a fire block above it.
     *
     * <p>This method checks if the block can safely have fire placed above it via {@link #canBurn(Block)},
     * then places a fire block above the target and tracks it as a temporary change (unless the fire
     * will burn permanently on materials like netherrack or soul sand).</p>
     *
     * @param block the solid block to set on fire (fire is placed above this block)
     */
    private void setBlockOnFire(@NotNull Block block) {
        Block above = block.getRelative(BlockFace.UP);

        if (canBurn(block)) {
            above.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);

            if (!staysLit(block)) { // only add the block if the fire will be temporary
                Ollivanders2API.getBlocks().addTemporarilyChangedBlock(above, this);
            }

            common.printDebugMessage("IncendioSuper.setBlockOnFire: setting " + block.getType() + " at " + block.getLocation().getX() + ", " + block.getLocation().getY() + ", " + block.getLocation().getZ() + " on fire", null, null, false);
            above.setType(Material.FIRE);
        }
    }

    /**
     * Can we set this block on fire?
     *
     * @param block the block to check
     * @return true if we can burn it, false otherwise
     */
    private boolean canBurn(@NotNull Block block) {
        Block above = block.getRelative(BlockFace.UP);

        if (Ollivanders2API.getBlocks().isTemporarilyChangedBlock(above)) {
            // above block is already transfigured
            common.printDebugMessage("IncendioSuper.canBurn: above block is already transfigured", null, null, false);
            return false;
        }
        else if (!materialAllowList.isEmpty() && !materialAllowList.contains(block.getType())) { // can happen with radius > 1
            // this is not an allowed material
            common.printDebugMessage("IncendioSuper.canBurn: block is not on the allow list", null, null, false);
            return false;
        }
        else if (!materialBlockedList.isEmpty() && materialBlockedList.contains(block.getType())) { // can happen with radius > 1
            // this is a blocked material
            common.printDebugMessage("IncendioSuper.canBurn: block is on the blocked list", null, null, false);
            return false;
        }
        else if (projectilePassThrough.contains(block.getType())) {
            // do not try to set pass-though blocks on fire
            common.printDebugMessage("IncendioSuper.canBurn: block is a pass-through block", null, null, false);
            return false;
        }
        else if (above.getType() != Material.AIR && above.getType() != Material.CAVE_AIR) {
            common.printDebugMessage("IncendioSuper.canBurn: above block is not air", null, null, false);
            return false;
        }

        return true;
    }

    /**
     * Determines if fire placed on this block should burn permanently.
     *
     * <p>Fire placed on certain flammable materials (netherrack and soul sand) will continue burning
     * indefinitely in Minecraft. This method checks if the block is one of these permanent-burn
     * materials. If so, the fire will not be tracked as a temporary change and will not be reverted
     * when the spell duration expires.</p>
     *
     * @param block the block to check (netherrack or soul sand will burn permanently)
     * @return true if fire on this block would stay lit forever due to the material
     */
    boolean staysLit(Block block) {
        return block.getType() == Material.NETHERRACK || block.getType() == Material.SOUL_SAND;
    }

    /**
     * Reverts all temporarily changed fire blocks back to air.
     *
     * <p>Called when the spell duration expires to clean up any fire blocks that were placed during
     * the spell's execution. Fire blocks placed on permanent burn materials (netherrack and soul sand)
     * are not tracked as temporary changes and will not be reverted, allowing them to continue burning
     * indefinitely.</p>
     */
    @Override
    public void revert() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }
}