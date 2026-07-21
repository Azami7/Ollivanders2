package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for potion-effect spells that fire without a projectile, affecting every eligible target within the
 * radius of the caster.
 *
 * @author Azami7
 */
public abstract class AddPotionEffectInRadius extends AddPotionEffect {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AddPotionEffectInRadius(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AddPotionEffectInRadius(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        noProjectile = true;
        affectSingleTarget = false;
    }
}
