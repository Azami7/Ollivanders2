package net.pottercraft.ollivanders2.spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
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
 * Base class for fire-setting spells that ignite blocks, living entities, and items in an area. Burn duration scales
 * with the caster's skill.
 * <p>
 * Temporary fire is removed when the spell ends, but fire placed on netherrack or soul sand burns permanently.
 * </p>
 *
 * @author Azami7
 */
public abstract class IncendioBase extends O2Spell {
    /**
     * The burn effect duration in ticks. Set by {@link #setBurnDuration()}.
     */
    private int burnDuration = 0;

    /**
     * If true, the spell ignites every target in radius; if false, only a single target and the block above the hit.
     */
    boolean strafe = false;

    /**
     * True once the burn effect has been applied; the spell then runs until {@link #burnDuration} elapses.
     */
    boolean burning = false;

    /**
     * Radius in blocks for igniting entities and items.
     */
    int entityRadius = 1;

    /**
     * Radius in blocks for igniting blocks when {@link #strafe} is set; otherwise only the target's top face burns.
     */
    int blockRadius = 1;

    /**
     * Scales how much caster skill affects burn duration; see {@link #setBurnDuration()} for the formula.
     */
    int durationModifier = 1;

    /**
     * Upper limit for {@link #burnDuration}, in ticks.
     */
    protected int maxBurnDuration = 30 * Ollivanders2Common.ticksPerSecond;

    /**
     * Lower limit for {@link #burnDuration}, in ticks.
     */
    protected int minBurnDuration = Ollivanders2Common.ticksPerSecond / 4;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public IncendioBase(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public IncendioBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
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
     * Ignite the target block (or all blocks in radius when strafing) along with the entities and items on top of it,
     * then keep the spell alive until the burn duration elapses. Kills the spell if no target block was hit.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
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
                for (Block block : BlockCommon.getBlocksInRadius(target.getLocation(), blockRadius))
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
     * Set {@link #burnDuration} to {@code usesModifier * durationModifier * ticksPerSecond}, limited to
     * [{@link #minBurnDuration}, {@link #maxBurnDuration}].
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
     * Place fire above the given block if it can burn, tracking the fire for reversion unless it would stay lit
     * permanently.
     *
     * @param block the solid block to set on fire; the fire is placed in the space above it
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
     * @param block the block to check
     * @return true if fire on this block would burn permanently (netherrack or soul sand), so it should not be
     *         tracked for reversion
     */
    boolean staysLit(Block block) {
        return block.getType() == Material.NETHERRACK || block.getType() == Material.SOUL_SAND;
    }

    /**
     * Extinguish all fire blocks this spell temporarily placed. Fire on permanent-burn materials is left lit.
     */
    @Override
    public void revert() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }
}