package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Protego Diabolica: a dark-arts stationary spell that rings the caster in magical fire, harming anyone not in the
 * caster's house who crosses it. The caster and their housemates are unharmed.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Diabolica">Harry Potter Wiki - Protego Diabolica</a>
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
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

    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA(p, caster.getUniqueId(), location, radius, duration);
    }
}
