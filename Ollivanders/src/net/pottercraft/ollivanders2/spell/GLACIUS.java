package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Freezing Charm that transforms liquids into solid ice and obsidian.
 *
 * <p>GLACIUS is a block transfiguration spell that conjures a blast of freezing cold air,
 * transforming water, lava, and ice blocks into frozen or solidified alternatives. The radius
 * and duration of the freezing effect scale with the caster's spell skill level.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target materials:</strong> WATER, LAVA, ICE</li>
 * <li><strong>Transformations:</strong>
 *   <ul>
 *   <li>WATER → ICE (freezes water)</li>
 *   <li>LAVA → OBSIDIAN (solidifies lava)</li>
 *   <li>ICE → PACKED_ICE (further freezes ice)</li>
 *   </ul>
 * </li>
 * <li><strong>Radius:</strong> 1-5 blocks, scaled by player skill (50% modifier)</li>
 * <li><strong>Duration:</strong> Temporary; 30 seconds to 10 minutes based on skill (100% modifier)</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>GLACIUS is particularly useful for creating safe passages through lava, protecting water-based
 * structures, and creating ice bridges. Affected blocks revert to their original forms after the
 * spell duration expires.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Freezing_Spell">Freezing Spell on Harry Potter Wiki</a>
 */
public final class GLACIUS extends GlaciusSuper {
    /**
     * Minimum effect radius for GLACIUS (1 block).
     *
     * <p>The spell's radius is clamped to not go below this value, ensuring a minimum
     * area of effect even at low skill levels.</p>
     */
    static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for GLACIUS (5 blocks).
     *
     * <p>The spell's radius is clamped to not exceed this value, establishing an upper
     * bound on the area of effect even at maximum skill levels.</p>
     */
    static final int maxRadiusConfig = 5;

    /**
     * Minimum spell duration for GLACIUS (30 seconds).
     *
     * <p>The spell's duration is clamped to not go below this value, ensuring transformed
     * blocks remain frozen for at least a minimum time.</p>
     */
    static final int minDurationConfig = 30 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum spell duration for GLACIUS (10 minutes).
     *
     * <p>The spell's duration is clamped to not exceed this value, establishing an upper
     * bound on how long blocks remain frozen.</p>
     */
    static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including name, branch (CHARMS), and flavor text
     * describing the Freezing Charm.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
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
     * Constructor for casting GLACIUS spells.
     *
     * <p>Initializes GLACIUS with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 1-5 blocks (50% skill modifier)</li>
     * <li>Duration: 30 seconds to 10 minutes (100% skill modifier)</li>
     * <li>Target materials: WATER, LAVA, ICE</li>
     * <li>Effect: Converts water to ice, lava to obsidian, ice to packed ice</li>
     * </ul>
     *
     * <p>Affected blocks are temporarily frozen; they revert to their original material
     * when the spell duration expires.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
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