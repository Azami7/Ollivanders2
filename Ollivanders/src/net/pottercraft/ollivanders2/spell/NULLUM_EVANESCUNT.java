package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Nullum Evanescunt is an anti-apparition charm that prevents players from apparating out of a protected area.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Prevents apparition out of the protected area</li>
 * <li>Works in conjunction with Nullum Apparebit for two-way apparition blocking</li>
 * <li>Shares the same radius and duration configuration as Nullum Apparebit</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT
 */
public final class NULLUM_EVANESCUNT extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public NULLUM_EVANESCUNT(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.NULLUM_EVANESCUNT;
        branch = O2MagicBranch.CHARMS;

        text = "Nullum evanescunt creates a stationary spell which will not allow disapparition out of it.";
    }

    /**
     * Constructs a new NULLUM_EVANESCUNT spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required. Uses the same min/max configuration as NULLUM_APPAREBIT.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public NULLUM_EVANESCUNT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.NULLUM_EVANESCUNT;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 15;
        radiusModifier = 1;
        flairSize = 10;
        noProjectile = true;

        // make min/max radius and duration the same as Nullum Apparebit
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.maxRadiusConfig;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.minRadiusConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.maxDurationConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.minDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new NULLUM_EVANESCUNT stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT(p, caster.getUniqueId(), location, radius, duration);
    }
}