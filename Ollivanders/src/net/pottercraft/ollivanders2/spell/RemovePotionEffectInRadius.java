package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for potion-effect-removal spells that fire without a projectile, removing effects from every eligible
 * target within the radius of the caster.
 *
 * @see AddPotionEffectInRadius
 */
public class RemovePotionEffectInRadius extends RemovePotionEffect {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RemovePotionEffectInRadius(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RemovePotionEffectInRadius(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        doFlair = true;
        noProjectile = true;
        effectRadius = 5;
        affectsMultiple = true;
    }
}
