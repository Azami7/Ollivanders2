package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Makes a spell projectile that creates a shield that hurts any entities within 0.5 meters of the spell wall.
 * <p>
 * https://harrypotter.fandom.com/wiki/Protego_Maxima
 * <p>
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 */
public final class PROTEGO_MAXIMA extends StationarySpell
{
    // todo make this match the book - https://harrypotter.fandom.com/wiki/Protego_Maxima
    private static int minDamage;
    private static int maxDamage;

    double damage;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_MAXIMA(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.PROTEGO_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" -Filius Flitwick");
            add("A Stronger Shield Charm");
        }};

        text = "Protego maxima is a stationary spell which will hurt any entities close to it's boundary.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PROTEGO_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 10;
        radiusModifier = 1;
        flairSize = 10;
        centerOnCaster = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.maxDurationConfig;
        minDamage = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.minDamageConfig;
        maxDamage = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.maxDamageConfig;

        initSpell();
    }

    /**
     * Set the damage this shield does based on caster's experience.
     */
    @Override
    void doInitSpell()
    {
        damage = (usesModifier / 4);

        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;
    }

    @Override
    protected O2StationarySpell createStationarySpell()
    {
        return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA(p, player.getUniqueId(), location, radius, duration, damage);
    }
}