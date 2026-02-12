package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Muggle-Repelling -Repello Muggletum - is a charm that prevents Muggles from seeing or entering an area. Any
 * non-magic person gets close to the vicinity of the enchantment remembers something urgent to do and leave.
 * <p>
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm</a>
 * @since 2.21
 */
public final class REPELLO_MUGGLETON extends StationarySpell {
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

        text = "Repello Muggleton will conceal players inside of it from Muggles and prevent them entering the area.";
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