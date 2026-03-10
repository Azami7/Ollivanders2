package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Muffliato is a charm that creates a zone of muffled silence around the caster.
 * Only players within the affected area can hear other players within the area.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Players inside the spell's radius can only hear other players inside the spell</li>
 * <li>Players outside cannot hear conversations inside the spell</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Muffliato_Charm">https://harrypotter.fandom.com/wiki/Muffliato_Charm</a>
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
     * Constructs a new MUFFLIATO spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
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
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new MUFFLIATO stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO(p, caster.getUniqueId(), location, radius, duration);
    }
}