package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * The Water-Making Spell that conjures clean, drinkable water.
 *
 * <p>AGUAMENTI is a water conjuration spell that creates water blocks where cast. It conjures a
 * stream of clean, drinkable water from the wand tip, suitable for drinking or creating water
 * sources. The spell works by converting air blocks to water, allowing it to fill empty spaces.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target:</strong> AIR and CAVE_AIR (pass-through blocks)</li>
 * <li><strong>Effect:</strong> Converts AIR to WATER</li>
 * <li><strong>Radius:</strong> 1 block (single-target precision spell)</li>
 * <li><strong>Duration:</strong> Temporary; 15 seconds to 10 minutes based on skill</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * <li><strong>Pass-Through:</strong> Can transfigure normally solid pass-through blocks</li>
 * <li><strong>Visual Effect:</strong> Blue ice particle stream (water jet)</li>
 * </ul>
 *
 * <p>AGUAMENTI is particularly useful for creating water sources for drinking, filling containers,
 * or creating decorative water features. Unlike most transfiguration spells, it is designed to work
 * on empty air blocks. The spell targets the block before the air block hit (the solid surface
 * behind the air) to place water adjacent to that surface.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Water-Making_Spell">Water-Making Spell on Harry Potter Wiki</a>
 */
public final class AGUAMENTI extends BlockTransfiguration {
    /**
     * Minimum effect radius for AGUAMENTI (1 block).
     *
     * <p>AGUAMENTI is a precision single-target spell with fixed radius of 1 block.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for AGUAMENTI (1 block).
     *
     * <p>AGUAMENTI is a precision single-target spell with fixed radius of 1 block.</p>
     */
    private static final int maxRadiusConfig = 1;

    /**
     * Minimum spell duration for AGUAMENTI (15 seconds).
     *
     * <p>The spell's duration is clamped to not go below this value, ensuring water
     * remains for at least a minimum time.</p>
     */
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum spell duration for AGUAMENTI (10 minutes).
     *
     * <p>The spell's duration is clamped to not exceed this value, establishing an upper
     * bound on how long the conjured water persists.</p>
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
     * describing the Water-Making Spell.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public AGUAMENTI(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AGUAMENTI;
        branch = O2MagicBranch.CHARMS;

        text = "Aguamenti will cause water to erupt against the surface you cast it on.";
        flavorText = new ArrayList<>() {{
            add("The Water-Making Spell conjures clean, drinkable water from the end of the wand.");
            add("The Water-Making Spell");
        }};
    }

    /**
     * Constructor for casting AGUAMENTI spells.
     *
     * <p>Initializes AGUAMENTI with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Radius: Fixed 1 block (single-target precision spell)</li>
     * <li>Duration: 15 seconds to 10 minutes (25% skill modifier)</li>
     * <li>Target materials: AIR and CAVE_AIR (pass-through blocks enabled)</li>
     * <li>Effect: Converts air to water</li>
     * <li>Blocked materials: Hot blocks (lava, fire, etc.) and existing water</li>
     * <li>Visual Effect: Blue ice particle stream (water jet)</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>AGUAMENTI is designed to create water sources for drinking or decorative purposes.
     * It uses a special targeting mechanism to place water adjacent to solid surfaces.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public AGUAMENTI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AGUAMENTI;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        effectsPassThrough = true;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.25; // 25% of usesModifier
        successMessage = "Water appears.";
        failureMessage = "Nothing seems to happen.";

        moveEffectData = Material.BLUE_ICE;

        // set materials that cannot be t by this spell
        materialBlockedList.addAll(Ollivanders2Common.getHotBlocks());
        materialBlockedList.add(Material.WATER);

        // make sure none of these are on the pass-through list
        projectilePassThrough.removeAll(materialBlockedList);

        // what type blocks transfigure in to for this spell
        transfigureType = Material.WATER;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Override of canTransfigure because Aguamenti targets the block above the projectile target, which can only be
     * AIR or CAVE_AIR. {@link BlockTransfiguration#canTransfigure(Block)} uses {@link O2Spell#materialBlockedList} and
     * {@link O2Spell#materialAllowList} to determine what block types can be changed but in this case those will be the
     * wrong types.
     *
     * @param block the block to validate
     * @return true if success check passes, the block is AIR or CAVE_AIR, and the block is not already transfigured
     */
    @Override
    boolean canTransfigure(@NotNull Block block) {
        common.printDebugMessage("Aguamenti.canTranfigure: Checking if this block can be transfigured.", null, null, false);

        // first check success rate
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand >= successRate) {
            common.printDebugMessage("Aguamenti.canTranfigure: " + player.getName() + " failed success check in canTransfigure()", null, null, false);
            return false;
        }
        else if (Ollivanders2API.getBlocks().isTemporarilyChangedBlock(block)) {
            // do not change if this block is already magically altered, this must be checked first because below conditions may also be true
            common.printDebugMessage("BlockTransfigure.canTranfigure: Block is already magically altered", null, null, false);
            return false;
        }

        return block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR;
    }

    /**
     * Overrides target block selection to target the solid block before the air.
     *
     * <p>AGUAMENTI places water adjacent to solid surfaces by targeting the block before
     * the pass-through air block. When the projectile hits an air block, this method returns
     * the solid block that precedes it in the projectile's path, allowing water to be placed
     * next to the surface rather than in the empty air.</p>
     *
     * <p>This special targeting is necessary because AGUAMENTI is designed to create water
     * sources at surfaces, not fill arbitrary air spaces.</p>
     *
     * @return the block before the air block (solid surface), or null if no target hit
     */
    @Override
    @Nullable
    public Block getTargetBlock() {
        if (hasHitTarget()) {
            // we want to target block before this block
            return location.clone().subtract(super.vector).getBlock();
        }

        return null;
    }
}
