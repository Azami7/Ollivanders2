package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Nullum Apparebit is an anti-apparition charm that prevents players from apparating into a protected area.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Prevents apparition into the protected area</li>
 * <li>Works in conjunction with Nullum Evanescunt for two-way apparition blocking</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT
 */
public final class NULLUM_APPAREBIT extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public NULLUM_APPAREBIT(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.NULLUM_APPAREBIT;
        branch = O2MagicBranch.CHARMS;

        text = "Nullum apparebit creates a stationary spell which will not allow apparition into it.";
    }

    /**
     * Constructs a new NULLUM_APPAREBIT spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.NULLUM_APPAREBIT;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 15;
        radiusModifier = 1;
        flairSize = 10;
        noProjectile = true;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.maxRadiusConfig;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.minRadiusConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.maxDurationConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.minDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new NULLUM_APPAREBIT stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT(p, caster.getUniqueId(), location, radius, duration);
    }
}