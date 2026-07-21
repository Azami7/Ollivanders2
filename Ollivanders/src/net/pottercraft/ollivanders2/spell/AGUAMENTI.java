package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * The Water-Making Spell: conjures a temporary water block against the surface it is cast at.
 * <p>
 * Unusually for a transfiguration, it acts on air rather than solid blocks — it places water in the air block the
 * projectile passes through, against the solid block behind it.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Water-Making_Spell">Harry Potter Wiki - Water-Making Spell</a>
 */
public final class AGUAMENTI extends BlockTransfiguration {
    private static final int minRadiusConfig = 1;

    private static final int maxRadiusConfig = 1;

    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
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
        materialBlockedList.add(Material.SOUL_FIRE);
        materialBlockedList.add(Material.SOUL_CAMPFIRE);
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
            common.printDebugMessage("Aguamenti.canTranfigure: " + caster.getName() + " failed success check in canTransfigure()", null, null, false);
            return false;
        }
        else if (Ollivanders2API.getBlocks().isTemporarilyChangedBlock(block)) {
            // do not change if this block is already magically altered, this must be checked first because below conditions may also be true
            common.printDebugMessage("BlockTransfigure.canTranfigure: Block is already magically altered", null, null, false);
            return false;
        }

        return BlockCommon.isAirBlock(block);
    }

    /**
     * Target the solid block immediately before the air block the projectile hit, so the water is placed against that
     * surface rather than in open air.
     *
     * @return the block before the hit air block, or null if the projectile has not hit a block
     */
    @Override
    @Nullable
    public Block getTargetBlock() {
        if (hasHitBlock()) {
            // we want to target block before this block
            return location.clone().subtract(super.vector).getBlock();
        }

        return null;
    }
}
