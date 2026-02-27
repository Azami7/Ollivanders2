package net.pottercraft.ollivanders2.spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2API;
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
 * Sets fire to blocks or living entities for an amount of time depending on the player's spell level.
 */
public abstract class IncendioSuper extends O2Spell {
    /**
     * The time remaining for the burn effect.
     */
    private int timeRemaining;

    /**
     * Does this spell strafe
     */
    boolean strafe = false;

    /**
     * Is this spell actively burning things
     */
    boolean burning = false;

    /**
     * The radius of entities the spell will affect
     */
    int radius = 1;

    /**
     * The radius of blocks the spell will affect
     */
    int blockRadius = 1;

    /**
     * A modifier on the duration for handling levels of this spell
     */
    int durationModifier = 1;

    /**
     * The max duration this spell can ever be
     */
    int maxDuration = 30 * Ollivanders2Common.ticksPerSecond;

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

        timeRemaining = (int) (usesModifier * durationModifier * Ollivanders2Common.ticksPerSecond);
        if (timeRemaining > maxDuration)
            timeRemaining = maxDuration;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.BUILD);
            worldGuardFlags.add(Flags.LIGHTER);
            worldGuardFlags.add(Flags.PVP);
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
        }
    }

    /**
     * Set entities or the target block on fire or, if already burning, countdown the duration of the burn effect.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        if (burning) {
            timeRemaining = timeRemaining - 1;

            if (timeRemaining <= 0)
                kill();
        }
        else {
            Block target = getTargetBlock();
            if (target == null) {
                common.printDebugMessage("IncendioSuper.doCheckEffect: target block is null", null, null, true);
                kill();
                return;
            }

            // blocks
            if (!strafe) {
                Block above = target.getRelative(BlockFace.UP);
                setBlockOnFire(above);
            }
            else {
                for (Block block : Ollivanders2Common.getBlocksInRadius(target.getLocation(), blockRadius))
                    setBlockOnFire(block);
            }

            // items
            List<Item> items = getNearbyItems(radius);
            for (Item item : items) {
                item.setFireTicks((int) timeRemaining);

                if (!strafe)
                    break;
            }

            // entities
            List<LivingEntity> living = getNearbyLivingEntities(radius);
            for (LivingEntity live : living) {
                live.setFireTicks(timeRemaining);

                if (!strafe)
                    break;
            }

            burning = true;
        }
    }

    /**
     * Set an air block to fire
     *
     * @param block the block to change
     */
    private void setBlockOnFire(@NotNull Block block) {
        Material type = block.getType();
        if (type == Material.AIR && !Ollivanders2API.getBlocks().isTemporarilyChangedBlock(block)) {
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);

            if (!staysLit(block)) { // only add the block if the fire will be temporary
                Ollivanders2API.getBlocks().addTemporarilyChangedBlock(block, this);
            }

            block.setType(Material.FIRE);
        }
    }

    /**
     * Is this fire on a base block that should burn forever?
     *
     * @param block the block to check
     * @return true if the block under this block would stay lit forever
     */
    boolean staysLit(Block block) {
        Block down = block.getRelative(BlockFace.DOWN);
        if (down.getType() == Material.NETHERRACK || down.getType() == Material.SOUL_SAND)
            return true;

        return false;
    }

    /**
     * Change fire blocks back to air
     */
    @Override
    public void revert() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }
}