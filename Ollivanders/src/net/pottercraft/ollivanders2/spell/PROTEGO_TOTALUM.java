package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Protego Totalum is a comprehensive shield spell that prevents any entities from crossing its boundary.
 * It also prevents hostile mobs and entities from spawning within the protected area.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Prevents players and hostile entities from crossing the shield boundary</li>
 * <li>Prevents any living entity from spawning within the protected area</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_totalum">https://harrypotter.fandom.com/wiki/Protego_totalum</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM
 */
public final class PROTEGO_TOTALUM extends StationarySpell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_TOTALUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PROTEGO_TOTALUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Raising her wand, she began to walk in a wide circle around Harry and Ron, murmuring incantations as she went. Harry saw little disturbances in the surrounding air: it was as if Hermione had cast a heat haze across their clearing.");
        }};

        text = "Protego totalum is a stationary spell which will prevent players or hostile entities from crossing its boundary and any living entity spawning in the area.";
    }

    /**
     * Constructs a new PROTEGO_TOTALUM spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PROTEGO_TOTALUM;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 10;
        radiusModifier = 1;
        flairSize = 10;
        noProjectile = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.maxDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new PROTEGO_TOTALUM stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM(p, caster.getUniqueId(), location, radius, duration);
    }
}