package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A stronger variant of the Freezing Charm with doubled effect radius.
 *
 * <p>GLACIUS_DUO is an enhanced version of GLACIUS that trades spell duration for significantly
 * increased area of effect. It freezes liquids and ice blocks over twice the radius of standard
 * GLACIUS, making it ideal for quickly freezing large areas. However, the transfiguration effect
 * persists for half the duration of standard GLACIUS.</p>
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
 * <li><strong>Radius:</strong> 2-10 blocks (double GLACIUS), scaled by player skill (100% modifier)</li>
 * <li><strong>Duration:</strong> Temporary; 15 seconds to 5 minutes (half GLACIUS duration), 50% skill modifier</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>GLACIUS_DUO is particularly useful for large-scale terraforming, quickly creating safe
 * passages through extensive lava lakes, or freezing large water bodies. The reduced duration
 * trade-off is offset by the doubled effect radius, making it more efficient for area-based tasks.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Glacius_Duo">Glacius Duo on Harry Potter Wiki</a>
 */
public final class GLACIUS_DUO extends GlaciusSuper {
    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including name, branch (CHARMS), and flavor text
     * describing the stronger Freezing Charm variant.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
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
     * Constructor for casting GLACIUS_DUO spells.
     *
     * <p>Initializes GLACIUS_DUO with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 2-10 blocks (double GLACIUS, 100% skill modifier)</li>
     * <li>Duration: 15 seconds to 5 minutes (half GLACIUS, 50% skill modifier)</li>
     * <li>Target materials: WATER, LAVA, ICE</li>
     * <li>Effect: Converts water to ice, lava to obsidian, ice to packed ice</li>
     * </ul>
     *
     * <p>GLACIUS_DUO excels at quickly freezing large areas at the cost of shorter duration.
     * Affected blocks are temporarily frozen; they revert to their original material when the
     * spell duration expires.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public GLACIUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.GLACIUS_DUO;
        branch = O2MagicBranch.CHARMS;

        minEffectRadius = GLACIUS.minRadiusConfig * 2;
        maxEffectRadius = GLACIUS.maxRadiusConfig * 2;
        effectRadiusModifier = 1;
        minDuration = GLACIUS.minDurationConfig / 2;
        maxDuration = GLACIUS.maxDurationConfig / 2;
        durationModifier = 0.5; // 50% of usesModifier

        initSpell();
    }
}