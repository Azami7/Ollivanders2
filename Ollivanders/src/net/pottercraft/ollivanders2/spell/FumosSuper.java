package net.pottercraft.ollivanders2.spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Causes blindness in a radius
 * <p>
 * https://harrypotter.fandom.com/wiki/Smokescreen_Spell
 */
public abstract class FumosSuper extends AddPotionEffectInRadius
{
    /**
     * The minimum radius to affect
     */
    int minRadius = 5;

    /**
     * The maximum radius that can be affected
     */
    int maxRadius = 20;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FumosSuper(Ollivanders2 plugin)
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
    public FumosSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        // material black list
        materialBlockedList.add(Material.WATER);
        materialBlockedList.add(Material.LAVA);
        materialBlockedList.add(Material.FIRE);

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        effectTypes.add(PotionEffectType.BLINDNESS);

        maxAmplifier = 1;
        minDurationInSeconds = 15;
        maxDurationInSeconds = 120;
    }
}