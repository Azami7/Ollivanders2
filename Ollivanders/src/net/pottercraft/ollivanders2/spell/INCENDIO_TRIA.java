package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The strongest fire-making charm that ignites a large area with intense flames.
 *
 * <p>INCENDIO_TRIA is the most powerful variant of INCENDIO with the largest area of effect and
 * longest burn duration. It uses strafe mode to affect all blocks and entities within a 4-block radius,
 * making it devastating against groups of enemies or for clearing large areas of burnable blocks.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 * <li><strong>Target:</strong> Multiple targets - strafe behavior enabled</li>
 * <li><strong>Block Radius:</strong> 4 blocks - affects a large area of blocks</li>
 * <li><strong>Entity Radius:</strong> 2 blocks - affects multiple entities in the area</li>
 * <li><strong>Duration Modifier:</strong> 4x - quadruple the baseline burn duration</li>
 * <li><strong>Max Burn Duration:</strong> 30 seconds (600 ticks)</li>
 * </ul>
 *
 * <p>The spell will set all blocks and entities on fire within the effective radius, with burn duration
 * scaled by the caster's spell proficiency and quadrupled compared to basic INCENDIO. The large 4-block
 * block radius makes it effective for clearing forests or large structures.</p>
 *
 * @see IncendioSuper
 * @see <a href="https://harrypotter.fandom.com/wiki/Incendio_Tria">https://harrypotter.fandom.com/wiki/Incendio_Tria</a>
 */
public final class INCENDIO_TRIA extends IncendioSuper {
    private static final int maxBurnDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public INCENDIO_TRIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.INCENDIO_TRIA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"Incendio!\" said Mr Weasley, pointing his wand at the hole in the wall behind him. Flames rose at once in the fireplace, crackling merrily as though they had been burning for hours.");
            add("The Strongest Fire-Making Charm");
        }};

        text = "Incendio Tria will burn blocks and entities it passes by. It's radius is four times that of Incendio and it's duration one quarter.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public INCENDIO_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.INCENDIO_TRIA;
        branch = O2MagicBranch.CHARMS;

        strafe = true;
        entityRadius = 2;
        blockRadius = 4;
        durationModifier = 4;
        maxBurnDuration = maxBurnDurationConfig;

        initSpell();
    }
}