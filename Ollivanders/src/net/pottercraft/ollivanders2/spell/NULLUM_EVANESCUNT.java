package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Nullum Evanescunt is an anti-apparition charm that prevents players from apparating out of a skill-scaled radius
 * around the caster for a limited duration. Pairs with {@link NULLUM_APPAREBIT}, which blocks apparating in, and
 * shares its radius and duration configuration.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">Harry Potter Wiki - Anti-Disapparition Jinx</a>
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand the wand correctness factor
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
     * @return a new NULLUM_EVANESCUNT stationary spell with this cast's radius and duration
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT(p, caster.getUniqueId(), location, radius, duration);
    }
}