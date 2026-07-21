package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Muffliato is a charm that creates a zone of muffled silence in a skill-scaled radius around the caster: only
 * players inside the zone can hear what is spoken inside it.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Muffliato_Charm">Harry Potter Wiki - Muffliato Charm</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO
 */
public final class MUFFLIATO extends StationarySpell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MUFFLIATO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.MUFFLIATO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add(" [...] perhaps most useful of all, Muffliato, a spell that filled the ears of anyone nearby with an unidentifiable buzzing, so that lengthy conversations could be held in class without being overheard.");
        }};

        text = "Muffliato creates a stationary spell which only allows the people inside to hear anything spoken inside the effect.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand the wand correctness factor
     */
    public MUFFLIATO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MUFFLIATO;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 10;
        radiusModifier = 1;
        flairSize = 10;
        noProjectile = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO.maxDurationConfig;

        initSpell();
    }

    /**
     * @return a new MUFFLIATO stationary spell with this cast's radius and duration
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO(p, caster.getUniqueId(), location, radius, duration);
    }
}