package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Gouging Charm, which mines a line of blocks in the direction it is cast.
 * <p>
 * When the projectile strikes a block, the spell digs forward one block at a time along the cast vector, breaking
 * each block naturally so it drops its normal items. Digging continues until the spell reaches a blocked material
 * (water, lava, or fire), a block that cannot be broken, or has mined its full length. The number of blocks mined
 * scales with the caster's skill, and the digging is throttled to a few blocks per second rather than happening
 * all at once.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Gouging_Spell">Harry Potter Wiki - Gouging Spell</a>
 */
public final class DEFODIO extends O2Spell {
    /**
     * The maximum depth this spell can dig
     */
    private static final int maxDepth = 10;

    /**
     * The number of blocks remaining to be mined
     */
    private int remainingCount;

    /**
     * The current block position
     */
    private Block curBlock = null;

    /**
     * The fractional position of the dig, accumulated along the cast vector so non-axis-aligned casts still cross
     * block boundaries. The current {@link #curBlock} is derived from this.
     */
    private Location pathLocation = null;

    /**
     * Slow down defodio mining to 4 blocks per second
     */
    private int cooldown = Ollivanders2Common.ticksPerSecond / 4;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DEFODIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DEFODIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Gouging Spell enables a witch or wizard to carve through earth and stone with ease. From budding Herbologists digging for Snargaluff seedlings to treasure-hunting curse breakers uncovering ancient wizard tombs, the Gouging Spell makes all manner of heavy labour a matter of pointing a wand.");
            add("The Gouging Charm");
        }};

        text = "Mines a line of blocks.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DEFODIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DEFODIO;
        branch = O2MagicBranch.CHARMS;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        // material blocked list
        materialBlockedList.add(Material.WATER);
        materialBlockedList.add(Material.LAVA);
        materialBlockedList.add(Material.FIRE);

        // make sure none of these are on the pass-through list
        projectilePassThrough.removeAll(materialBlockedList);

        initSpell();
    }

    /**
     * Set the number of blocks to mine from the caster's skill.
     * <p>
     * The dig length grows by one block for every 4 points of {@code usesModifier}, then is clamped to the range
     * 1 to {@link #maxDepth} so an unskilled caster still mines at least one block and a highly skilled caster
     * cannot tunnel without limit.
     * </p>
     */
    @Override
    void doInitSpell() {
        remainingCount = (int) usesModifier / 4;

        if (remainingCount > maxDepth)
            remainingCount = maxDepth;
        else if (remainingCount < 1)
            remainingCount = 1;

        common.printDebugMessage("Defodio remaining set to " + remainingCount, null, null, false);
    }

    /**
     * Mine one block per throttle interval along the projectile's vector once it has hit a block.
     * <p>
     * Waits until the projectile strikes a block, then on each throttle interval breaks the current block and
     * advances to the next block along the cast vector. The spell ends when it reaches a blocked material, a block
     * that fails to break, or has mined its full length. While the projectile is still in flight, or during the
     * cooldown between mined blocks, this does nothing.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        // use cooldown to slow defodio to 4 blocks per second
        if (cooldown > 0) {
            cooldown = cooldown - 1;
            return;
        }

        if (curBlock == null) {
            curBlock = getTargetBlock();

            // start the fractional accumulator at the impact block so movement carries between mined blocks; clone so
            // advancing it never mutates the block's own location (Block.getLocation() may hand back a shared instance)
            if (curBlock != null)
                pathLocation = curBlock.getLocation().clone();
        }

        if (curBlock == null) { // only happens if hasHitBlock is false, which can't happen here but not having this causes a linting error
            kill();
            return;
        }

        // stop the spell if we hit a block type on the blocked list or when the max depth is reached
        if (materialBlockedList.contains(curBlock.getType()) || remainingCount <= 0) {
            common.printDebugMessage("Block type not allowed: " + curBlock.getType(), null, null, false);
            kill();
            return;
        }

        // stop the spell if something prevented the current block breaking naturally
        if (!curBlock.breakNaturally()) {
            kill();
            return;
        }
        remainingCount = remainingCount - 1;
        common.printDebugMessage("Blocks remaining: " + remainingCount, null, null, false);

        // advance along the vector to the next distinct block, accumulating fractional movement so non-axis-aligned
        // casts still cross block boundaries instead of re-targeting the block we just broke
        Block nextBlock = curBlock;
        while (nextBlock.equals(curBlock)) {
            pathLocation.add(vector);
            nextBlock = pathLocation.getBlock();
        }
        curBlock = nextBlock;

        cooldown = Ollivanders2Common.ticksPerSecond / 4;
    }

    /**
     * Get the number of blocks this cast will mine, as set by {@link #doInitSpell()} from the caster's skill and
     * clamped to the range 1 to {@link #getMaxDepth()}.
     *
     * <p>This value is only meaningful before the spell resolves: it is decremented as each block is mined, so
     * read it before the spell ticks to its effect.</p>
     *
     * @return the number of blocks remaining to mine
     */
    public int getRemainingCount() {
        return remainingCount;
    }

    /**
     * Get the maximum number of blocks any cast can mine, regardless of how high the caster's skill is.
     *
     * @return the maximum dig length
     */
    public int getMaxDepth() {
        return maxDepth;
    }
}