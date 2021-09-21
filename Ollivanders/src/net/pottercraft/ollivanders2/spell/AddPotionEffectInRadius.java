package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Spells that add a potion effect to all targets within a radius of the caster - not a spell projectile
 *
 * @author Azami7
 */
public class AddPotionEffectInRadius extends AddPotionEffect
{
    /**
     * Radius of the spell from the caster.
     */
    int radius = 5;

    /**
     * Whether the spell targets the caster
     */
    boolean targetSelf = false;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AddPotionEffectInRadius(Ollivanders2 plugin)
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
    public AddPotionEffectInRadius(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        durationInSeconds = minDurationInSeconds;
    }

    /**
     * If a target player is within the radius of the caster, add the potion effect to the player.
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        if (targetSelf)
        {
            addEffectsToTarget(player);
        }
        else
        {
            affectRadius(radius, true);
        }

        kill();
    }
}
