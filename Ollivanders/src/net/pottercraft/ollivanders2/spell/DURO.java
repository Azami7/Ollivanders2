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
 * The Hardening Charm that transforms blocks into solid stone.
 *
 * <p>DURO is a block transfiguration spell that casts the Hardening Charm, converting most
 * materials into solid stone. The spell is useful for creating barriers, hardening structures,
 * and defensive maneuvers. The radius and duration of the hardening effect scale with the
 * caster's spell skill level.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target:</strong> Most block types except hot/flammable materials (water, lava, fire)</li>
 * <li><strong>Effect:</strong> Converts blocks to STONE</li>
 * <li><strong>Radius:</strong> 1-15 blocks, scaled by player skill (25% modifier)</li>
 * <li><strong>Duration:</strong> Temporary; reverts after 15 seconds to 10 minutes based on skill</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>DURO is particularly useful for quickly hardening soft materials like dirt and sand, creating
 * stone barriers, or protecting structures. Hot blocks (lava and fire) cannot be hardened, preventing
 * unintended interactions with dangerous materials. Affected blocks revert to their original forms
 * after the spell duration expires.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Hardening_Charm">Hardening Charm on Harry Potter Wiki</a>
 */
public final class DURO extends BlockTransfiguration {
    /**
     * Minimum effect radius for DURO (1 block).
     *
     * <p>The spell's radius is clamped to not go below this value, ensuring a minimum
     * area of effect even at low skill levels.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for DURO (15 blocks).
     *
     * <p>The spell's radius is clamped to not exceed this value, establishing an upper
     * bound on the area of effect even at maximum skill levels.</p>
     */
    private static final int maxRadiusConfig = 15;

    /**
     * Minimum spell duration for DURO (15 seconds).
     *
     * <p>The spell's duration is clamped to not go below this value, ensuring hardened
     * blocks remain stone for at least a minimum time.</p>
     */
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum spell duration for DURO (10 minutes).
     *
     * <p>The spell's duration is clamped to not exceed this value, establishing an upper
     * bound on how long blocks remain hardened into stone.</p>
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
     * describing the Hardening Charm.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public DURO(Ollivanders2 plugin) {
        // todo make a duro variant that "hardens" tools
        super(plugin);

        spellType = O2SpellType.DURO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Hardening Charm");
            add("The Hardening Charm will turn an object into solid stone. This can be surprisingly handy in a tight spot. Of course, most students only seem to use this spell to sabotage their fellow students' schoolbags or to turn a pumpkin pasty to stone just before someone bites into it. It is unwise to try this unworthy trick on any of your teachers.");
        }};

        text = "Duro will turn the blocks in a radius in to stone.";
    }

    /**
     * Constructor for casting DURO spells.
     *
     * <p>Initializes DURO with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 1-15 blocks (25% skill modifier)</li>
     * <li>Duration: 15 seconds to 10 minutes (50% skill modifier)</li>
     * <li>Target material: STONE (blocks transform to solid stone)</li>
     * <li>Blocked materials: Hot blocks (lava, fire, etc.)</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>Blocks affected by DURO are temporarily hardened into stone; they revert to their
     * original material when the spell duration expires.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public DURO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DURO;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.25; // 25%
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.5; // 50%

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        // materials that cannot be targeted by this spell
        materialBlockedList.addAll(Ollivanders2Common.getHotBlocks());

        // what type blocks transfigure in to for this spell
        transfigureType = Material.STONE;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}