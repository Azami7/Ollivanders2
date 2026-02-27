package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The strongest Freezing Charm variant with massive effect radius at minimal duration.
 *
 * <p>GLACIUS_TRIA is the most powerful variant of the Freezing Charm, with four times the effect
 * radius of standard GLACIUS. This extreme area-of-effect spell is designed for rapidly freezing
 * massive regions, making it invaluable for large-scale terrain modification. However, the
 * transfiguration effect persists for only one quarter the duration of standard GLACIUS.</p>
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
 * <li><strong>Radius:</strong> 4-20 blocks (quadruple GLACIUS), scaled by player skill (200% modifier)</li>
 * <li><strong>Duration:</strong> Temporary; 7.5 seconds to 2.5 minutes (quarter GLACIUS duration), 25% skill modifier</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>GLACIUS_TRIA is most effective for one-time, large-scale freezing operations. The extremely
 * short duration trade-off is offset by the massive 4x radius, allowing rapid transformation of
 * huge water or lava lakes in a single cast.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Glacius_Tria">Glacius Tria on Harry Potter Wiki</a>
 */
public final class GLACIUS_TRIA extends GlaciusSuper {
    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including name, branch (CHARMS), and flavor text
     * describing the strongest Freezing Charm variant.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
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
     * Constructor for casting GLACIUS_TRIA spells.
     *
     * <p>Initializes GLACIUS_TRIA with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 4-20 blocks (quadruple GLACIUS, 200% skill modifier)</li>
     * <li>Duration: 7.5 seconds to 2.5 minutes (quarter GLACIUS, 25% skill modifier)</li>
     * <li>Target materials: WATER, LAVA, ICE</li>
     * <li>Effect: Converts water to ice, lava to obsidian, ice to packed ice</li>
     * </ul>
     *
     * <p>GLACIUS_TRIA excels at freezing massive areas in a single cast. The extremely short
     * duration is a trade-off for the quadrupled effect radius. Affected blocks are temporarily
     * frozen; they revert to their original material when the spell duration expires.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public GLACIUS_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.GLACIUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        minEffectRadius = GLACIUS.minRadiusConfig * 4;
        maxEffectRadius = GLACIUS.maxRadiusConfig * 4;
        effectRadiusModifier = 2.0; // 200% of usesModifier
        minDuration = GLACIUS.minDurationConfig / 4;
        maxDuration = GLACIUS.maxDurationConfig / 4;
        durationModifier = 0.25; // 25% of usesModifier

        initSpell();
    }
}