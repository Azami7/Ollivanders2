package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Makes an anti-disapparition spell that players cannot apparate in to.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx
 */
public final class NULLUM_APPAREBIT extends StationarySpell
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public NULLUM_APPAREBIT(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.NULLUM_APPAREBIT;
        branch = O2MagicBranch.CHARMS;

        text = "Nullum apparebit creates a stationary spell which will not allow apparition into it.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.NULLUM_APPAREBIT;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 15;
        radiusModifier = 1;
        flairSize = 10;
        centerOnCaster = true;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.maxRadiusConfig;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.minRadiusConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.maxDurationConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.minDurationConfig;

        initSpell();
    }

    @Override
    protected O2StationarySpell createStationarySpell()
    {
        return new net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT(p, player.getUniqueId(), location, radius, duration);
    }
}