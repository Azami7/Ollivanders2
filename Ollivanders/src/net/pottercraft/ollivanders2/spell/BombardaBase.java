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
 * Base class for explosion spells that break blocks in a skill-scaled radius, up to a per-spell blast-resistance and
 * hardness limit.
 *
 * @author Azami7
 */
public abstract class BombardaBase extends O2Spell {

    /**
     * The highest block blast resistance this spell can break.
     *
     * @see <a href="https://mcreator.net/wiki/list-block-resistance-levels">MCreator - block resistance levels</a>
     */
    double maxBlastResistance = 1.0;

    /**
     * The highest block hardness this spell can break, checked after blast resistance.
     *
     * @see <a href="https://mcreator.net/wiki/list-hardness-values-blocks">MCreator - block hardness values</a>
     */
    double maxHardness = 1.0;

    /**
     * If true, this spell can break doors regardless of their blast resistance and hardness (which in Minecraft
     * exceed the usual thresholds), matching the books where some Bombarda casts blow doors open.
     */
    boolean breaksDoors = false;

    /**
     * The radius of blocks the spell breaks. Set by {@link #calculateEffectRadius()}.
     */
    double effectRadius = 1.0;

    /**
     * Lower limit for {@link #effectRadius}.
     */
    double minEffectRadius = 1.0;

    /**
     * Upper limit for {@link #effectRadius}.
     */
    double maxEffectRadius = 1.0;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    BombardaBase(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
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
     * Detonate at the target, breaking every breakable block within the skill-scaled radius, then end the spell.
     */
    protected void doCheckEffect() {
        if (hasHitBlock()) {
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
     * Set {@link #effectRadius} to {@code usesModifier * 0.2}, limited to [{@link #minEffectRadius},
     * {@link #maxEffectRadius}].
     */
    void calculateEffectRadius() {
        effectRadius = usesModifier * 0.2; // 20% of uses modifier

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Check whether a block can be broken: not an unbreakable material and within the spell's blast-resistance and
     * hardness thresholds. Doors are broken only when {@link #breaksDoors} is set, regardless of those thresholds.
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
     * @return true if this spell can break doors
     */
    public boolean doesBreakDoors() {
        return breaksDoors;
    }
}