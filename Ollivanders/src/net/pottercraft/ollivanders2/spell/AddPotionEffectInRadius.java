package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for instant-radius potion effect spells.
 *
 * <p>These spells apply potion effects to all targets within a radius of the caster without
 * using a projectile. The spell fires immediately and affects multiple targets based on the
 * configured radius and target filtering settings.</p>
 */
public abstract class AddPotionEffectInRadius extends AddPotionEffect {
    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AddPotionEffectInRadius(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor for casting the spell.
     *
     * <p>Configures the spell as an instant radius effect with no projectile. All eligible
     * targets within the calculated radius are affected.</p>
     *
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
