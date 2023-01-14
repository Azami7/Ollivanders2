package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Protego horribilis stops spells entering the protected area, except the Killing Curse (which is too strong).
 * <p>
 * https://harrypotter.fandom.com/wiki/Protego_horribilis
 * <p>
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 */
public final class PROTEGO_HORRIBILIS extends StationarySpell
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_HORRIBILIS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.PROTEGO_HORRIBILIS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add(" [...] although he could barely see out of it, he pointed his wand through the smashed window and started muttering incantations of great complexity. Harry heard a weird rushing noise, as though Flitwick had unleashed the power of the wind into the grounds.");
        }};

        text = "Protego horribilis is a stationary spell which will destroy any spells crossing it's barrier.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PROTEGO_HORRIBILIS;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 10;
        radiusModifier = 1;
        flairSize = 10;
        centerOnCaster = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.maxRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.minRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.maxDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.minDurationConfig;

        initSpell();
    }

    @Override
    protected O2StationarySpell createStationarySpell()
    {
        return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS(p, player.getUniqueId(), location, radius, duration);
    }
}