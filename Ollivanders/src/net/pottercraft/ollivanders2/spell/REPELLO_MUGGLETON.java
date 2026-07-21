package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Muggle-Repelling Charm — conceals the area around the caster from Muggles and drives approaching Muggles away with
 * the sense of an urgent task elsewhere.
 * <p>
 * Casts a stationary spell centered on the caster (no projectile); its radius and duration scale with the caster's
 * experience, within the configured bounds. The concealment behavior lives in the stationary spell
 * {@link net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON}.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">Muggle-Repelling Charm</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON
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
        noProjectile = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.maxDurationConfig;

        initSpell();
    }

    /**
     * Create the stationary REPELLO_MUGGLETON spell with the calculated radius and duration.
     *
     * @return the stationary spell to register
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON(p, caster.getUniqueId(), location, radius, duration);
    }
}