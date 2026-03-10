package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Molliare is a cushioning charm that softens the ground in a protected area, eliminating all fall damage.
 * Anyone falling within the spell's radius will land safely without injury.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a stationary spell centered on the caster's location</li>
 * <li>Eliminates all fall damage within the protected area</li>
 * <li>Softens the landing surface for a smooth, painless descent</li>
 * <li>No projectile required - cast directly on the caster</li>
 * <li>Radius range: varies by configuration</li>
 * <li>Duration range: varies by configuration (scales with caster experience)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cushioning_Charm">https://harrypotter.fandom.com/wiki/Cushioning_Charm</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.MOLLIARE
 */
public final class MOLLIARE extends StationarySpell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MOLLIARE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.MOLLIARE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Cushioning Charm.");
            add("Harry felt himself glide back toward the ground as though weightless, landing painlessly on the rocky passage floor.");
        }};

        text = "Molliare softens the ground in a radius around the site.  All fall damage will be negated in this radius.";
    }

    /**
     * Constructs a new MOLLIARE spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters including duration modifier, radius modifier,
     * and min/max values for radius and duration. The spell is cast centered on the caster
     * with no projectile required. Duration modifier is very short (1 second per level).</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MOLLIARE;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 1;
        radiusModifier = 1;
        flairSize = 10;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.MOLLIARE.maxRadiusConfig;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.MOLLIARE.minRadiusConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.MOLLIARE.maxDurationConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.MOLLIARE.minDurationConfig;

        initSpell();
    }

    /**
     * Creates the stationary spell instance.
     *
     * <p>Instantiates a new MOLLIARE stationary spell with the calculated radius and duration.</p>
     *
     * @return the created stationary spell instance
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.MOLLIARE(p, caster.getUniqueId(), location, radius, duration);
    }
}