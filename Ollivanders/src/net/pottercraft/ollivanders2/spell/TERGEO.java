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
 * The siphoning spell that removes water blocks.
 *
 * <p>TERGEO is a block transfiguration spell that siphons away water by converting it to air.
 * It targets all water blocks within a radius and converts them to air, effectively removing
 * large bodies of water with a single cast.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target:</strong> WATER blocks only</li>
 * <li><strong>Effect:</strong> Converts WATER to AIR</li>
 * <li><strong>Radius:</strong> 1-20 blocks, scaled by player skill (20% modifier)</li>
 * <li><strong>Duration:</strong> Temporary; reverts after 15 seconds to 10 minutes based on skill</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>The spell's effectiveness increases with player skill, affecting a larger radius of water
 * blocks per cast. Duration scales with skill level, determining how long the transfiguration
 * persists before reverting the water blocks.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Wiping_Spell">Wiping Spell on Harry Potter Wiki</a>
 */
public final class TERGEO extends BlockTransfiguration {
    /**
     * Minimum effect radius for TERGEO (1 block).
     *
     * <p>The spell's radius is clamped to not go below this value, ensuring a minimum
     * area of effect even at low skill levels.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for TERGEO (20 blocks).
     *
     * <p>The spell's radius is clamped to not exceed this value, establishing an upper
     * bound on the area of effect even at maximum skill levels.</p>
     */
    private static final int maxRadiusConfig = 20;

    /**
     * Minimum spell duration for TERGEO (15 seconds).
     *
     * <p>The spell's duration is clamped to not go below this value, ensuring transfigured
     * water remains siphoned for at least a minimum time.</p>
     */
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum spell duration for TERGEO (10 minutes).
     *
     * <p>The spell's duration is clamped to not exceed this value, establishing an upper
     * bound on how long water siphoning can persist.</p>
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
    public TERGEO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.TERGEO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Siphoning Spell");
            add("The wand siphoned off most of the grease. Looking rather pleased with himself, Ron handed the slightly smoking handkerchief to Hermione.");
            add("She raised her wand, said “Tergeo!” and siphoned off the dried blood.");
        }};

        text = "Tergeo will siphon water where it hits.";
    }

    /**
     * Constructor for casting TERGEO spells.
     *
     * <p>Initializes TERGEO with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 1-20 blocks (20% skill modifier)</li>
     * <li>Duration: 15 seconds to 10 minutes (50% skill modifier)</li>
     * <li>Target material: WATER only (all other materials blocked)</li>
     * <li>Effect: Converts water to air</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>Water blocks affected by TERGEO are temporarily changed; they revert to water
     * when the spell duration expires.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public TERGEO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.TERGEO;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.2; // 20% of usesModifier
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.5; // 50% of usesModifier

        // pass-through
        projectilePassThrough.remove(Material.WATER);

        // set materials that can be transfigured by this spell
        materialAllowList.add(Material.WATER);

        // what type blocks transfigure in to for this spell
        transfigureType = Material.AIR;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}