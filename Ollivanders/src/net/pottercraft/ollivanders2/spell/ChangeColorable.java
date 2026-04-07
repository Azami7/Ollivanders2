package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for colour change spells that recolor sheep or colorable blocks.
 *
 * <p>When the projectile encounters a living entity, the spell checks for sheep and recolors the first
 * one found. If no entities are nearby and the projectile hits a colorable block, the block's color is
 * changed instead. Non-colorable targets produce a failure message.</p>
 *
 * <p>Subclasses set {@link #color} to determine the target color. See {@link O2Color#isColorable(Material)}
 * for the list of supported block types.</p>
 *
 * @author Azami7
 * @see O2Color#changeColor(Material, O2Color)
 * @see <a href="https://harrypotter.fandom.com/wiki/Colour_Change_Charm">Harry Potter Wiki - Colour Change Charm</a>
 */
public abstract class ChangeColorable extends O2Spell {
    /**
     * The color to apply to the target entity or block. Defaults to {@link O2Color#WHITE}.
     */
    O2Color color = O2Color.WHITE;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    ChangeColorable(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    ChangeColorable(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);
    }

    /**
     * Check for a nearby sheep to recolor, or recolor the target block if no entities are found.
     */
    @Override
    protected void doCheckEffect() {
        // first try to recolor any sheep in range
        List<LivingEntity> entities = getNearbyLivingEntities(defaultRadius);

        if (!entities.isEmpty()) {
            boolean changed = false;
            for (LivingEntity livingEntity : entities) {
                if (livingEntity instanceof Sheep) {
                    Sheep sheep = (Sheep) livingEntity;
                    sheep.setColor(color.getDyeColor());

                    changed = true;
                    break;
                }
            }
            kill();

            if (!changed)
                sendFailureMessage();
        }
        else if (hasHitTarget()) {
            kill();

            Block target = getTargetBlock();
            if (target == null) {
                common.printDebugMessage("ChangeColorable.doCheckEffect: target block is null", null, null, true);
                return;
            }

            if (O2Color.isColorable(target.getType())) {
                Material newColor = O2Color.changeColor(target.getType(), color);
                target.setType(newColor);
            }
            else
                sendFailureMessage();
        }
    }

    /**
     * Get the color this spell applies to its target.
     *
     * @return the target {@link O2Color}
     */
    public O2Color getColor() {
        return color;
    }
}