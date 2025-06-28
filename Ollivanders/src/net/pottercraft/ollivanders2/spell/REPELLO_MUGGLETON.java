package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Repello Muggleton will hide any blocks and players in its radius from those outside of it.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm</a>
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 */
public final class REPELLO_MUGGLETON extends StationarySpell {
    // todo make this match the book - https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm
    // todo as written this spell is https://harrypotter.fandom.com/wiki/Cave_inimicum

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REPELLO_MUGGLETON(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REPELLO_MUGGLETON;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Muggle-Repelling Charms on every inch of it. Every time Muggles have got anywhere near here all year, they've suddenly remembered urgent appointments and had to dash away again.");
        }};

        text = "Repello Muggleton will hide any blocks and players in it's radius from those outside of it.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPELLO_MUGGLETON;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 15;
        radiusModifier = 1;
        flairSize = 10;
        centerOnCaster = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.maxDurationConfig;

        initSpell();
    }

    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON(p, player.getUniqueId(), location, radius, duration);
    }
}