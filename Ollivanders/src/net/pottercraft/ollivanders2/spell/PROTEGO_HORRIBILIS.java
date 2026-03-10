package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Protego Horribilis is a powerful shield spell that blocks dark arts spells from entering a protected area.
 * However, extremely powerful spells like the Killing Curse may still penetrate the shield.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Blocks most dark arts spells from crossing the shield boundary</li>
 * <li>Extremely powerful spells (like the Killing Curse) may bypass the shield</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_horribilis">https://harrypotter.fandom.com/wiki/Protego_horribilis</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS
 */
public final class PROTEGO_HORRIBILIS extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_HORRIBILIS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PROTEGO_HORRIBILIS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add(" [...] although he could barely see out of it, he pointed his wand through the smashed window and started muttering incantations of great complexity. Harry heard a weird rushing noise, as though Flitwick had unleashed the power of the wind into the grounds.");
        }};

        text = "Protego horribilis is a stationary spell which protects against dark arts spells.";
    }

    /**
     * Constructs a new PROTEGO_HORRIBILIS spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PROTEGO_HORRIBILIS;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 10;
        radiusModifier = 1;
        flairSize = 10;
        noProjectile = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.maxDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new PROTEGO_HORRIBILIS stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS(p, caster.getUniqueId(), location, radius, duration);
    }
}