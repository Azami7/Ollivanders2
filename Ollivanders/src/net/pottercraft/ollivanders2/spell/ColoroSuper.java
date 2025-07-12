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
 * Change a sheep or a colorable block to a specific color.
 */
public abstract class ColoroSuper extends O2Spell {
    O2Color color = O2Color.WHITE;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    ColoroSuper(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    ColoroSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);
    }

    /**
     * Look for colorable entities or blocks, if one is found, change its color
     */
    @Override
    protected void doCheckEffect() {
        // first try to recolor any sheep in range
        List<LivingEntity> entities = getNearbyLivingEntities(defaultRadius);

        if (entities.size() > 0) {
            for (LivingEntity livingEntity : entities) {
                if (livingEntity instanceof Sheep) {
                    Sheep sheep = (Sheep) livingEntity;
                    sheep.setColor(color.getDyeColor());

                    kill();
                    return;
                }
            }

            return;
        }

        if (hasHitTarget()) {
            Block target = getTargetBlock();
            if (target == null) {
                common.printDebugMessage("ColoroSuper.doCheckEffect: target block is null", null, null, true);
                kill();
                return;
            }

            if (O2Color.isColorable(target.getType())) {
                Material newColor = O2Color.changeColor(target.getType(), color);
                target.setType(newColor);
            }

            kill();
        }
    }
}