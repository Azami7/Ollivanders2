package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Remove a potion effect for all entities in a radius of the caster
 */
public class RemovePotionEffectInRadius extends RemovePotionEffect
{
    /**
     * Radius of the spell from the caster.
     */
    int radius = 5;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RemovePotionEffectInRadius(Ollivanders2 plugin)
    {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RemovePotionEffectInRadius(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
    }

    /**
     * Remove the potion effect from the caster.
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        affectRadius(radius, true);

        kill();
    }
}
