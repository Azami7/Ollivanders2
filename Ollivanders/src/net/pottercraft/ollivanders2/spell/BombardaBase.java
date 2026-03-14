package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for explosion spells that break blocks.
 *
 * <p>Bombarda spells create explosions that destroy blocks in a radius based on the caster's spell level.
 * Blocks can only be broken if they meet blast resistance and hardness thresholds.</p>
 *
 * <p>Subclasses configure spell-specific thresholds and behavior via the protected fields.</p>
 *
 * @author Azami7
 */
public abstract class BombardaBase extends O2Spell {

    /**
     * The maximum blast resistance a block can have and be affected by Bombarda.
     *
     * <ul>
     *     <li>Stone, brick, and similar - 6</li>
     *     <li>Planks, fences, doors, and similar - 3</li>
     *     <li>Wood - 2</li>
     *     <li>Concrete - 1.8</li>
     *     <li>Bookshelves - 1.5</li>
     *     <li>Sandstone - 0.8</li>
     * </ul>
     *
     * @see <a href="https://mcreator.net/wiki/list-block-resistance-levels">https://mcreator.net/wiki/list-block-resistance-levels</a>
     */
    double maxBlastResistance = 1.0;

    /**
     * The maximum hardness a block can be and be affected by Bombarda. This is checked *after* blast resistance.
     *
     * <ul>
     *     <li>Obsidian - 50</li>
     *     <li>Anvil, bell, iron objects, raw ore - 5</li>
     *     <li>Wood doors/trapdoors - 3</li>
     *     <li>Bricks, cobblestone, logs, planks - 2</li>
     *     <li>All stone, bookshelf, concrete - 1.5</li>
     *     <li>Banners, signs - 1</li>
     *     <li>Sandstone - 0.8</li>
     * </ul>
     *
     * @see <a href="https://mcreator.net/wiki/list-hardness-values-blocks">https://mcreator.net/wiki/list-hardness-values-blocks</a>
     */
    double maxHardness = 1.0;

    /**
     * Does this spell break doors? Doors are specifically mentioned in HP as breaking for some
     * users of Bombarda. In MC, doors have a blast resistance and hardness greater than concrete (which really doesn't make sense).
     */
    boolean breaksDoors = false;

    /**
     * The radius of blocks the spell will attempt to break.
     */
    double effectRadius = 1.0;

    /**
     * The minimum radius that can be affected by this spell.
     */
    double minEffectRadius = 1.0;

    /**
     * The maximum radius that can be affected by this spell.
     */
    double maxEffectRadius = 1.0;

    /**
     * Constructor for spell info generation. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    BombardaBase(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor to cast a Bombarda spell.
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting the spell
     * @param rightWand the wand being used
     */
    BombardaBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.TNT);
            worldGuardFlags.add(Flags.OTHER_EXPLOSION);
            worldGuardFlags.add(Flags.BLOCK_BREAK);
        }
    }

    /**
     * Spawns an explosion and breaks blocks within the calculated radius.
     *
     * <p>Only breaks blocks that pass the {@code canBreak()} validation.</p>
     */
    protected void doCheckEffect() {
        if (hasHitTarget()) {
            calculateEffectRadius();

            // play explosion particle and sound
            world.spawnParticle(Particle.EXPLOSION, location, 1);
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

            // break any blocks we can in the radius
            List<Block> blocksInRadius = BlockCommon.getBlocksInRadius(location, effectRadius);
            for (Block block : blocksInRadius) {
                if (canBreak(block)) {
                    block.breakNaturally();
                }
            }

            kill();
        }
    }

    /**
     * Calculates the explosion radius based on spell level.
     *
     * <p>Radius is 20% of the caster's uses modifier, clamped between min and max bounds.</p>
     */
    void calculateEffectRadius() {
        effectRadius = usesModifier * 0.2; // 20% of uses modifier

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Determines if a block can be broken by this spell.
     *
     * <p>A block can be broken if:</p>
     *
     * <ul>
     * <li>It's not in the unbreakable materials list</li>
     * <li>If it's a door, the spell must have {@code breaksDoors = true}</li>
     * <li>Its blast resistance does not exceed {@code maxBlastResistance}</li>
     * <li>Its hardness does not exceed {@code maxHardness}</li>
     * </ul>
     *
     * @param block the block to check
     * @return true if the block can be broken, false otherwise
     */
    boolean canBreak(Block block) {
        Material blockType = block.getType();

        // is it unbreakable?
        if (Ollivanders2Common.getUnbreakableMaterials().contains(blockType))
            return false;

        // is it a door and we can break doors?
        // doors are a specific case to check because specifically mentioned
        // in HP as breakable by some casts of bombarda (also they have a really high hardness in MC)
        if (breaksDoors && Ollivanders2Common.getDoors().contains(blockType))
            return true;

        // does the block have too high of blast resistance?
        if (blockType.getBlastResistance() > maxBlastResistance)
            return false;

        // does the block have too high of hardness?
        return (blockType.getHardness() <= maxHardness);
    }

    /**
     * Checks if this spell breaks door blocks.
     *
     * @return true if this spell can break doors, false otherwise
     */
    public boolean doesBreakDoors() {
        return breaksDoors;
    }
}