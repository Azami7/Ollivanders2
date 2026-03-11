package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Protego Diabolica is a dark arts spell that creates a protective ring of magical fire
 * around the caster that harms anyone not in their house who crosses it.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Fires damage to Muggles and students from other houses who enter the protected area</li>
 * <li>Does not harm the caster or students from the same house</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: 5-10 blocks</li>
 * <li>Duration range: 5-30 minutes (scales with caster experience)</li>
 * </ul>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Diabolica">https://harrypotter.fandom.com/wiki/Protego_Diabolica</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA
 */
public final class PROTEGO_DIABOLICA extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_DIABOLICA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PROTEGO_DIABOLICA;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("Loyalty Shield");
            add("\"It is a very powerful dark charm. It creates a protective ring of black fire that harms enemies of the caster.\"");
        }};

        text = "Protego diabolica creates a ring of magical fire around the caster that will harm anyone who is not in their house who crosses it.";
    }

    /**
     * Constructs a new PROTEGO_DIABOLICA spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public PROTEGO_DIABOLICA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PROTEGO_DIABOLICA;
        branch = O2MagicBranch.DARK_ARTS;

        durationModifierInSeconds = 60;
        radiusModifier = 1;
        flair = false;
        noProjectile = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA.maxDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new PROTEGO_DIABOLICA stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA(p, caster.getUniqueId(), location, radius, duration);
    }
}
