package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Glacius Tria has the same effect as Glacius but with 4x the duration and radius.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Glacius_Tria
 */
public final class GLACIUS_TRIA extends GlaciusSuper
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public GLACIUS_TRIA(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.GLACIUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
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
    public GLACIUS_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.GLACIUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        minRadius = GLACIUS.minRadiusConfig * 4;
        maxRadius = GLACIUS.maxRadiusConfig * 4;
        radiusModifier = 2.0; // 200% of usesModifier
        minDuration = GLACIUS.minDurationConfig / 4;
        maxDuration = GLACIUS.maxDurationConfig / 4;
        durationModifier = 0.25; // 25% of usesModifier

        initSpell();
    }
}