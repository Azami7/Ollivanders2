package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WATER_BREATHING;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Carcerem Aquaticum - The Water Orb Spell.
 *
 * <p>CARCEREM_AQUATICUM traps the target player in a protective orb of non-flowing water, immobilizing them
 * while preventing drowning damage. The spell expands the player's bounding box by 1 block in all directions
 * and fills all air blocks within that region with water, preventing movement while allowing the player to
 * breathe safely through the WATER_BREATHING effect. The spell only affects players at normal or reduced
 * size (scale ≤ 1.0).</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Only targets players with scale attribute ≤ 1.0</li>
 * <li>Expands player's bounding box by 1 block in all directions</li>
 * <li>Converts all air blocks within the expanded region to non-flowing water</li>
 * <li>Preserves any non-air blocks that exist within the region</li>
 * <li>Applies WATER_BREATHING effect to prevent drowning during immobilization</li>
 * <li>Automatically reverts all water blocks after effect duration expires</li>
 * <li>Uses partial immobilization (allows rotation but prevents movement)</li>
 * <li>Minimum effect duration: 2 minutes (ensures water breathing lasts through cleanup)</li>
 * </ul>
 *
 * <p>Reference: <a href="https://harrypotter.fandom.com/wiki/Orb_of_Water">Harry Potter Wiki - Orb of Water</a></p>
 */
public class CARCEREM_AQUATICUM extends ImmobilizePlayerSuper {
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
     * be trapped in the water orb due to the expanded bounding box being too small to contain them.</p>
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
     * Create water blocks around the target player based on their bounding box.
     *
     * <p>Expands the target player's bounding box by 1 block in all directions, then converts
     * all air blocks within that expanded region to non-flowing water. Only air blocks are changed,
     * preserving any existing non-air blocks. All changed blocks are tracked as temporarily changed
     * so they can be reverted when the effect expires.</p>
     *
     * @param target the player to surround with water blocks
     */
    void createWaterBlocks(Player target) {
        Levelled waterData = (Levelled) Bukkit.createBlockData(Material.WATER);
        waterData.setLevel(0);

        List<Block> blocks = calculateBlocksToChange(target.getBoundingBox().expand(1.0));
        for (Block block : blocks) {

            if (BlockCommon.isAirBlock(block)) {
                Ollivanders2API.getBlocks().addTemporarilyChangedBlock(block, this);
                block.setType(Material.WATER);
                block.setBlockData(waterData);
            }
        }
    }

    /**
     * Calculate all blocks within the expanded bounding box region.
     *
     * <p>Iterates through all integer block coordinates within the given bounding box and collects
     * the Block objects. These blocks will be filtered later to only change air blocks to water.</p>
     *
     * @param boundingBox the expanded bounding box region to collect blocks from
     * @return a list of all Block objects within the bounding box coordinates
     */
    List<Block> calculateBlocksToChange(BoundingBox boundingBox) {
        ArrayList<Block> blocks = new ArrayList<>();

        int minX = (int) Math.floor(boundingBox.getMinX());
        int minY = (int) Math.floor(boundingBox.getMinY());
        int minZ = (int) Math.floor(boundingBox.getMinZ());
        int maxX = (int) Math.floor(boundingBox.getMaxX());
        int maxY = (int) Math.floor(boundingBox.getMaxY());
        int maxZ = (int) Math.floor(boundingBox.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);

                    if (!blocks.contains(block))
                        blocks.add(block);
                }
            }
        }

        return blocks;
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
}
