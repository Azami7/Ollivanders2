package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Confundus Charm super class which causes confusion in the target
 *
 * @author Azami7
 * @author lownes
 */
public abstract class ConfundusSuper extends AddPotionEffect
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    ConfundusSuper(Ollivanders2 plugin)
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
    ConfundusSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        effectTypes.add(PotionEffectType.CONFUSION);

        maxAmplifier = 0;
        minDurationInSeconds = 15;
        maxDurationInSeconds = 120;
    }
}