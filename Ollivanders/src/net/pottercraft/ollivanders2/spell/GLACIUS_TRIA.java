package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The strongest {@link GLACIUS}, freezing four times the radius for one quarter the duration.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Glacius_Tria">Harry Potter Wiki - Glacius Tria</a>
 */
public final class GLACIUS_TRIA extends GlaciusBase {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public GLACIUS_TRIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.GLACIUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Strongest Freezing Charm");
        }};

        text = "Glacius Tria will freeze blocks in a radius four times that of glacius, but for one quarter the time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public GLACIUS_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.GLACIUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        if (Ollivanders2.testMode) { // in test mode, the full radius slows the tests too much
            minEffectRadius = GLACIUS.minRadiusConfig * 2;
            maxEffectRadius = GLACIUS.maxRadiusConfig * 2;
        }
        else {
            minEffectRadius = GLACIUS.minRadiusConfig * 4;
            maxEffectRadius = GLACIUS.maxRadiusConfig * 4;
        }
        effectRadiusModifier = 2.0; // 200% of usesModifier
        minDuration = GLACIUS.minDurationConfig / 4;
        maxDuration = GLACIUS.maxDurationConfig / 4;
        durationModifier = 0.25; // 25% of usesModifier

        initSpell();
    }
}