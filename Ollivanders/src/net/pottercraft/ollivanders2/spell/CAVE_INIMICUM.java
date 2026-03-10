package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Cave Inimicum produces a concealment shield that keeps players hidden from view.
 * Those who are outside the shield cannot see, hear, or interact with those inside.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Hides players inside the shield from players outside the shield</li>
 * <li>Prevents hostile mobs outside from detecting players inside</li>
 * <li>Sounds a proximity alarm when entities approach the shield boundary</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cave_inimicum">https://harrypotter.fandom.com/wiki/Cave_inimicum</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM
 */
public final class CAVE_INIMICUM extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CAVE_INIMICUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CAVE_INIMICUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"That's as much as I can do. At the very least, we should know they're coming, I can't guarantee it will keep out Vol—\" -Hermione Granger");
            add("The Concealment Shield");
        }};

        text = "Cave Inimicum will hide players inside of it from players and hostile mobs outside the shield. It will also sound a proximity alarm if they get close to the shield area";
    }

    /**
     * Constructs a new CAVE_INIMICUM spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public CAVE_INIMICUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.CAVE_INIMICUM;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 15;
        radiusModifier = 5;
        flairSize = 10;
        noProjectile = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.maxDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new CAVE_INIMICUM stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM(p, caster.getUniqueId(), location, radius, duration);
    }
}


