package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Repello Muggleton is a Muggle-repelling charm that conceals magical areas from non-magical people.
 * When Muggles approach the protected area, they are compelled to leave and remember an urgent task elsewhere.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Conceals players inside the spell's radius from Muggles outside</li>
 * <li>Prevents Muggles from entering the protected area</li>
 * <li>Muggles who approach become disoriented and must leave</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm</a>
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
     * Constructs a new REPELLO_MUGGLETON spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
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
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new REPELLO_MUGGLETON stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON(p, caster.getUniqueId(), location, radius, duration);
    }
}