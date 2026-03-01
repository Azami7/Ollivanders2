package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WATER_BREATHING;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Carcerem Aquaticum - The Water Orb Spell.
 *
 * <p>CARCEREM_AQUATICUM traps the target player in a protective orb of non-flowing water, immobilizing them
 * while preventing drowning damage. The spell creates a 3x3 grid of water blocks above, at, and below the
 * player's eye level, preventing movement while allowing the player to breathe safely through the
 * WATER_BREATHING effect. The spell only affects players at normal or reduced size (scale ≤ 1.0).</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Only targets players with scale attribute ≤ 1.0</li>
 * <li>Creates non-flowing water blocks in a 3x3 grid around player</li>
 * <li>Applies WATER_BREATHING effect to prevent drowning during immobilization</li>
 * <li>Automatically reverts water blocks after effect duration expires</li>
 * <li>Uses partial immobilization (allows rotation but prevents movement)</li>
 * <li>Minimum effect duration: 2 minutes (ensures water breathing lasts through cleanup)</li>
 * </ul>
 *
 * <p>Reference: <a href="https://harrypotter.fandom.com/wiki/Orb_of_Water">Harry Potter Wiki - Orb of Water</a></p>
 */
public class CARCEREM_AQUATICUM extends ImmobilizePlayerSuper {
    private static final double playerBlockHeight = 1.8;

    /**
     * Setting to 2 minutes, which is the min duration for the WATER_BREATHING effect
     */
    private static final int minEffectDurationConfig = 2 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CARCEREM_AQUATICUM(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CARCEREM_AQUATICUM;
        branch = O2MagicBranch.DEFENSE_AGAINST_THE_DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Water Orb Spell");
            add("\"The water in the pool rose up and covered Voldemort like a cocoon of molten glass.\"");
        }};

        text = "Creates an orb of water that surrounds the target player and immobilizes them.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using (correctness factor)
     */
    public CARCEREM_AQUATICUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        moveEffectData = Material.BLUE_ICE;

        spellType = O2SpellType.CARCEREM_AQUATICUM;
        branch = O2MagicBranch.DEFENSE_AGAINST_THE_DARK_ARTS;

        fullImmobilize = false;
        minEffectDuration = minEffectDurationConfig;

        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Determine if a player can be targeted by this spell.
     *
     * <p>Only players with a scale attribute of 1.0 or lower can be targeted. Oversized players cannot
     * be trapped in the water orb due to the fixed size of the water block grid.</p>
     *
     * @param target the player to validate as a potential target
     * @return true if the player's scale is ≤ 1.0, false if oversized (or if scale attribute is null)
     */
    boolean canTarget(Player target) {
        if (!Ollivanders2.testMode) {
            AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);

            if (scaleAttribute == null || scaleAttribute.getBaseValue() > 1.0) {
                common.printDebugMessage("CarceremAquaticum.canTarget: player scale > 1.0", null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Calculate the effective height of the target player in blocks.
     *
     * <p>Returns the player's height adjusted by their scale attribute. In test mode, always returns the
     * standard player height (1.8 blocks). In normal mode, multiplies the player's scale attribute by
     * the standard height to account for oversized or undersized players.</p>
     *
     * @param target the player whose height to calculate
     * @return the effective player height in blocks, adjusted for scale
     */
    double getPlayerHeight(Player target) {
        if (!Ollivanders2.testMode) {
            AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);

            double scale = 1.0;

            if (scaleAttribute != null) // this should always be true or canTarget() would have failed
                scale = scaleAttribute.getBaseValue();

            return playerBlockHeight * scale;
        }

        return playerBlockHeight;
    }

    /**
     * Create water blocks and breathing effect to trap and protect the target.
     *
     * <p>Creates non-flowing water blocks around the target player and applies a WATER_BREATHING effect to
     * prevent drowning. The water breathing effect lasts 10 ticks longer than the immobilization effect to
     * ensure it persists through cleanup. A scheduled task will automatically revert all water blocks after
     * the effect duration expires.</p>
     *
     * @param target the immobilized player to surround with water
     */
    void addAdditionalEffects(Player target) {
        // create water blocks around the player
        createWaterBlocks(target);

        // add water breathing with just over duration time to make sure it doesn't expire before we clean up the water blocks
        WATER_BREATHING waterBreathing = new WATER_BREATHING(p, effectDuration + 10, false, target.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(waterBreathing);

        // clean up the blocks
        new BukkitRunnable() {
            @Override
            public void run() {
                revertBlocks();
            }
        }.runTaskLater(p, effectDuration);
    }

    /**
     * Create water blocks in a 3x3 grid pattern above, at, and around the target player.
     *
     * <p>Creates rows of water blocks (3x3 grids) at three vertical levels: above the player's eyes,
     * at eye level, and below (if player height ≤ 2 blocks). This creates a complete water orb that
     * traps the player in place. All water blocks are set to level 0 (fully filled, non-flowing).</p>
     *
     * @param target the player to surround with water blocks
     */
    void createWaterBlocks(Player target) {
        Block headBlock = target.getEyeLocation().getBlock();

        createWaterBlockRow(headBlock.getRelative(BlockFace.UP));
        createWaterBlockRow(headBlock);

        double playerHeight = getPlayerHeight(target);
        if (playerHeight <= 2.0)
            createWaterBlockRow(headBlock.getRelative(BlockFace.DOWN));
    }

    /**
     * Create a 3x3 grid of non-flowing water blocks centered at the given block.
     *
     * <p>Creates a complete row of water blocks in a 3x3 pattern (center, north, south, east, west,
     * and diagonal corners) all set to level 0 (fully filled, non-flowing water). All blocks are tracked
     * as temporarily changed so they can be reverted when the effect ends.</p>
     *
     * @param center the center block around which to create the 3x3 water grid
     */
    void createWaterBlockRow(Block center) {
        Levelled waterData = (Levelled) Bukkit.createBlockData(Material.WATER);
        waterData.setLevel(0);

        for (Block block : getRowBlocks(center)) {
            Ollivanders2API.getBlocks().addTemporarilyChangedBlock(block, this);
            block.setType(Material.WATER);
            block.setBlockData(waterData);
        }
    }

    /**
     * Revert all water blocks created by this spell.
     *
     * <p>Reverts all temporarily changed blocks that were created as part of the water orb. This is
     * automatically called after the effect duration expires via a scheduled task.</p>
     */
    void revertBlocks() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }

    /**
     * Get all blocks in a 3x3 grid pattern around the center block.
     *
     * <p>Returns a list of 9 blocks: the center block plus 8 adjacent blocks forming a 3x3 square.
     * Includes the center, cardinal directions (north, south, east, west), and diagonal directions.</p>
     *
     * @param center the center block of the 3x3 grid
     * @return an ArrayList containing the 9 blocks in the 3x3 grid pattern
     */
    public ArrayList<Block> getRowBlocks(Block center) {
        ArrayList<Block> blocks = new ArrayList<>();

        blocks.add(center);
        blocks.add(center.getRelative(BlockFace.EAST));
        blocks.add(center.getRelative(BlockFace.WEST));
        blocks.add(center.getRelative(BlockFace.NORTH));
        blocks.add(center.getRelative(BlockFace.SOUTH));
        blocks.add(center.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST));
        blocks.add(center.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
        blocks.add(center.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST));
        blocks.add(center.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST));

        return blocks;
    }
}
