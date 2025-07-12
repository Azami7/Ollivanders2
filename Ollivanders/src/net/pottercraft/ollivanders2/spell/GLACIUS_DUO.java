package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Glacius Duo has the same effect as Glacius but with 2x the duration and radius.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Glacius_Duo">https://harrypotter.fandom.com/wiki/Glacius_Duo</a>
 */
public final class GLACIUS_DUO extends GlaciusSuper {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public GLACIUS_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.GLACIUS_DUO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A Stronger Freezing Charm");
            add("\"It's about preparing ourselves ...for what's waiting for us out there.\" -Hermione Granger");
        }};

        text = "Glacius Duo will freeze blocks in a radius twice that of glacius, but for half the time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public GLACIUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.GLACIUS_DUO;
        branch = O2MagicBranch.CHARMS;

        minRadius = GLACIUS.minRadiusConfig * 2;
        maxRadius = GLACIUS.maxRadiusConfig * 2;
        radiusModifier = 1; // 100% of usesModifier
        minDuration = GLACIUS.minDurationConfig / 2;
        maxDuration = GLACIUS.maxDurationConfig / 2;
        durationModifier = 0.5; // 50% of usesModifier

        initSpell();
    }
}