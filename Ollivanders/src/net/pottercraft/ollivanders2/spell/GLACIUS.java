package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Freezing Charm: transfigures water to ice, ice to packed ice, and lava to obsidian within a radius. Affected
 * blocks revert to their original material when the spell expires.
 *
 * @author Azami7
 * @see GlaciusBase
 * @see <a href="https://harrypotter.fandom.com/wiki/Freezing_Spell">Harry Potter Wiki - Freezing Spell</a>
 */
public final class GLACIUS extends GlaciusBase {
    /**
     * Base radius bounds and durations that the GLACIUS_DUO and GLACIUS_TRIA variants scale from.
     */
    static final int minRadiusConfig = 1;

    static final int maxRadiusConfig = 5;

    static final int minDurationConfig = 30 * Ollivanders2Common.ticksPerSecond;

    static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public GLACIUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.GLACIUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A charm that conjures a blast of freezing cold air from the end of the wand.");
            add("The Freezing Charm");
        }};

        text = "Turns water in to ice, ice to packed ice, and lava in to obsidian.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public GLACIUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.GLACIUS;
        branch = O2MagicBranch.CHARMS;

        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.5; // 50% of usesModifier
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0; // 100% of usesModifier

        initSpell();
    }
}