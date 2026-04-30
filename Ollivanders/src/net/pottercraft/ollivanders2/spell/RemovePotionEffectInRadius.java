package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for instant-radius potion effect removal spells.
 *
 * <p>These spells remove potion effects from all targets within a radius of the caster without
 * using a projectile. The spell fires immediately with a visual flair and affects multiple
 * targets based on the configured radius.</p>
 *
 * @see AddPotionEffectInRadius
 */
public class RemovePotionEffectInRadius extends RemovePotionEffect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RemovePotionEffectInRadius(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor for casting the spell.
     *
     * <p>Configures the spell as an instant radius effect with no projectile and visual flair.
     * All eligible targets within the configured radius are affected.</p>
     *
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
