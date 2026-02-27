package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Softening Charm that transforms blocks into bouncy sponges.
 *
 * <p>SPONGIFY is a block transfiguration spell that casts the Softening Charm, converting terrain
 * and structures into sponge blocks. The transfigured blocks become spongy and rubbery, creating
 * a bouncy surface within the spell's effect radius.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target:</strong> Most block types except water and heat-sensitive materials</li>
 * <li><strong>Effect:</strong> Converts blocks to SPONGE</li>
 * <li><strong>Radius:</strong> 1-15 blocks, scaled by player skill (25% modifier)</li>
 * <li><strong>Duration:</strong> Temporary; reverts after 15 seconds to 10 minutes based on skill</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>Blocked materials include water (to prevent interactions with liquid) and hot blocks
 * like lava and fire. The spell's effectiveness increases with player skill, affecting a larger
 * radius and persisting longer per cast.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Softening_Charm">Softening Charm on Harry Potter Wiki</a>
 */
public class SPONGIFY extends BlockTransfiguration {
    /**
     * Minimum effect radius for SPONGIFY (1 block).
     *
     * <p>The spell's radius is clamped to not go below this value, ensuring a minimum
     * area of effect even at low skill levels.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for SPONGIFY (15 blocks).
     *
     * <p>The spell's radius is clamped to not exceed this value, establishing an upper
     * bound on the area of effect even at maximum skill levels.</p>
     */
    private static final int maxRadiusConfig = 15;

    /**
     * Minimum spell duration for SPONGIFY (15 seconds).
     *
     * <p>The spell's duration is clamped to not go below this value, ensuring transformed
     * blocks remain sponge for at least a minimum time.</p>
     */
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum spell duration for SPONGIFY (10 minutes).
     *
     * <p>The spell's duration is clamped to not exceed this value, establishing an upper
     * bound on how long blocks remain as sponge.</p>
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
     * with quotes from the Harry Potter series.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public SPONGIFY(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SPONGIFY;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"Today's lesson will most assuredly involve learning how to cast the Softening Charm, Spongify.\" -Filius Flitwick");
            add("The Softening Charm");
        }};

        text = "Turns the blocks in a radius in to slime blocks.";
    }

    /**
     * Constructor for casting SPONGIFY spells.
     *
     * <p>Initializes SPONGIFY with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 1-15 blocks (25% skill modifier)</li>
     * <li>Duration: 15 seconds to 10 minutes (50% skill modifier)</li>
     * <li>Target material: SPONGE (blocks transform to bouncy sponge blocks)</li>
     * <li>Blocked materials: WATER and hot blocks (lava, fire, etc.)</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>Blocks affected by SPONGIFY are temporarily changed to sponge; they revert to their
     * original material when the spell duration expires.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public SPONGIFY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.SPONGIFY;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.25; // 25% of usesModifier
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.5; // 50% of usesModifier

        // what type blocks transfigure in to for this spell
        transfigureType = Material.SPONGE;

        // types of materials this spell cannot change or pass through
        materialBlockedList.add(Material.WATER);
        materialBlockedList.addAll(Ollivanders2Common.getHotBlocks());

        // make sure none of these are on the pass-through list
        projectilePassThrough.removeAll(materialBlockedList);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}
