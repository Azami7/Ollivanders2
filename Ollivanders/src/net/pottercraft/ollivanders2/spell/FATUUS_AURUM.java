package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Stone to Gold Charm that transforms stone blocks into precious gold.
 *
 * <p>FATUUS_AURUM is a block transfiguration spell that temporarily converts stone blocks into
 * glittering gold blocks. As the name suggests, the transformation is temporary—the gold reverts
 * to stone after the spell's duration expires. This spell can be used to create golden structures,
 * decorative elements, or wealth displays.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target:</strong> STONE blocks only</li>
 * <li><strong>Effect:</strong> Converts STONE to GOLD_BLOCK</li>
 * <li><strong>Radius:</strong> 1 block (single-target precision spell)</li>
 * <li><strong>Duration:</strong> Temporary; 15 seconds to 10 minutes based on skill level</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>FATUUS_AURUM is a utility charm for creating gold displays or temporary golden structures.
 * The single-block radius makes it ideal for precision work. Affected blocks revert to stone
 * when the spell duration expires, though the caster may recast to maintain the golden appearance.</p>
 *
 * @author Azami7
 */
public final class FATUUS_AURUM extends BlockTransfiguration {
    /**
     * Minimum effect radius for FATUUS_AURUM (1 block).
     *
     * <p>FATUUS_AURUM is a precision single-target spell with fixed radius of 1 block.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for FATUUS_AURUM (1 block).
     *
     * <p>FATUUS_AURUM is a precision single-target spell with fixed radius of 1 block.</p>
     */
    private static final int maxRadiusConfig = 1;

    /**
     * Minimum spell duration for FATUUS_AURUM (15 seconds).
     *
     * <p>The spell's duration is clamped to not go below this value, ensuring gold blocks
     * remain gold for at least a minimum time.</p>
     */
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum spell duration for FATUUS_AURUM (10 minutes).
     *
     * <p>The spell's duration is clamped to not exceed this value, establishing an upper
     * bound on how long blocks remain golden.</p>
     */
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including name, branch (CHARMS), and flavor text
     * with quotes about gold and deception from literature.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public FATUUS_AURUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FATUUS_AURUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("What glitters may not be gold; and even wolves may smile; and fools will be led by promises to their deaths.");
            add("There is thy gold, worse poison to men's souls.");
            add("Stone to Gold Charm");
        }};

        text = "Turns a stone block in to gold.";
    }

    /**
     * Constructor for casting FATUUS_AURUM spells.
     *
     * <p>Initializes FATUUS_AURUM with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Radius: Fixed 1 block (precision single-target spell)</li>
     * <li>Duration: 15 seconds to 10 minutes (based on skill level)</li>
     * <li>Target material: STONE blocks only</li>
     * <li>Effect: Converts stone to gold blocks</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>FATUUS_AURUM is a utility charm for creating temporary golden structures or displays.
     * The single-block radius makes it ideal for precision work, and the duration can be extended
     * by recasting the spell.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public FATUUS_AURUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FATUUS_AURUM;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        successMessage = "Gold!";

        // set materials that can be transfigured by this spell
        materialAllowList.add(Material.STONE);

        // what type blocks transfigure in to for this spell
        transfigureType = Material.GOLD_BLOCK;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}
