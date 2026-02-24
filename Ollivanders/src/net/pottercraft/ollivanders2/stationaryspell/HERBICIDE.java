package net.pottercraft.ollivanders2.stationaryspell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A stationary spell that destroys plants and vegetation within a protected area.
 *
 * <p>Herbicide creates a powerful killing field that eliminates all plant life by:</p>
 * <ul>
 *   <li>Replacing plants with dead alternatives (leaves become air, grass becomes dirt, etc.)</li>
 *   <li>Dropping items for saplings and vegetation</li>
 *   <li>Resetting crop growth to age 0</li>
 *   <li>Checking WorldGuard permissions to prevent griefing</li>
 * </ul>
 *
 * <p>The spell targets a comprehensive list of plant materials including flowers, crops, leaves,
 * mossy blocks, potted plants, and mushrooms.</p>
 *
 * @author Azami7
 */
public class HERBICIDE extends ThrownPotionStationarySpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (20 blocks).
     */
    public static final int maxRadiusConfig = 20;

    /**
     * Minimum spell duration (30 seconds).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Maximum spell duration (30 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Mapping of plant materials to their dead/destroyed replacements.
     *
     * <p>Used to determine what material each plant block becomes after herbicide effect.</p>
     */
    Map<Material, Material> blocksReplacements = new HashMap<>() {{
        put(Material.ACACIA_LEAVES, Material.AIR);
        put(Material.ALLIUM, Material.AIR);
        put(Material.AZALEA, Material.DEAD_BUSH);
        put(Material.AZALEA_LEAVES, Material.AIR);
        put(Material.AZURE_BLUET, Material.AIR);
        put(Material.BAMBOO_SAPLING, Material.AIR);
        put(Material.BIG_DRIPLEAF, Material.AIR);
        put(Material.BIG_DRIPLEAF_STEM, Material.AIR);
        put(Material.BIRCH_LEAVES, Material.AIR);
        put(Material.BLUE_ORCHID, Material.AIR);
        put(Material.BROWN_MUSHROOM, Material.AIR);
        put(Material.CAVE_VINES_PLANT, Material.AIR);
        put(Material.CHERRY_LEAVES, Material.AIR);
        put(Material.CHORUS_PLANT, Material.DEAD_BUSH);
        put(Material.CHORUS_FLOWER, Material.AIR);
        put(Material.CORNFLOWER, Material.AIR);
        put(Material.DANDELION, Material.AIR);
        put(Material.DARK_OAK_LEAVES, Material.AIR);
        put(Material.FERN, Material.AIR);
        put(Material.FLOWERING_AZALEA, Material.DEAD_BUSH);
        put(Material.FLOWERING_AZALEA_LEAVES, Material.AIR);
        put(Material.GLOW_LICHEN, Material.AIR);
        put(Material.GRASS_BLOCK, Material.DIRT);
        put(Material.JUNGLE_LEAVES, Material.AIR);
        put(Material.LARGE_FERN, Material.AIR);
        put(Material.LILAC, Material.AIR);
        put(Material.LILY_OF_THE_VALLEY, Material.AIR);
        put(Material.LILY_PAD, Material.AIR);
        put(Material.MANGROVE_LEAVES, Material.AIR);
        put(Material.MOSS_BLOCK, Material.AIR);
        put(Material.MOSS_CARPET, Material.AIR);
        put(Material.MOSSY_COBBLESTONE, Material.COBBLESTONE);
        put(Material.MOSSY_COBBLESTONE_SLAB, Material.COBBLESTONE_SLAB);
        put(Material.MOSSY_COBBLESTONE_STAIRS, Material.COBBLESTONE_STAIRS);
        put(Material.MOSSY_COBBLESTONE_WALL, Material.COBBLESTONE_WALL);
        put(Material.MOSSY_STONE_BRICK_SLAB, Material.STONE_BRICK_SLAB);
        put(Material.MOSSY_STONE_BRICK_STAIRS, Material.STONE_BRICK_STAIRS);
        put(Material.MOSSY_STONE_BRICK_WALL, Material.STONE_BRICK_WALL);
        put(Material.MOSSY_STONE_BRICKS, Material.STONE_BRICKS);
        put(Material.MUSHROOM_STEM, Material.AIR);
        put(Material.MYCELIUM, Material.DIRT);
        put(Material.OAK_LEAVES, Material.AIR);
        put(Material.ORANGE_TULIP, Material.AIR);
        put(Material.OXEYE_DAISY, Material.AIR);
        put(Material.PALE_HANGING_MOSS, Material.AIR);
        put(Material.PALE_MOSS_BLOCK, Material.DIRT);
        put(Material.PALE_MOSS_CARPET, Material.DIRT);
        put(Material.PALE_OAK_LEAVES, Material.AIR);
        put(Material.PEONY, Material.AIR);
        put(Material.PINK_PETALS, Material.AIR);
        put(Material.PINK_TULIP, Material.AIR);
        put(Material.POPPY, Material.AIR);
        put(Material.POTTED_ACACIA_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_ALLIUM, Material.FLOWER_POT);
        put(Material.POTTED_AZALEA_BUSH, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_AZURE_BLUET, Material.FLOWER_POT);
        put(Material.POTTED_BAMBOO, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_BIRCH_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_BLUE_ORCHID, Material.FLOWER_POT);
        put(Material.POTTED_BROWN_MUSHROOM, Material.FLOWER_POT);
        put(Material.POTTED_CACTUS, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_CHERRY_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_CLOSED_EYEBLOSSOM, Material.FLOWER_POT);
        put(Material.POTTED_CORNFLOWER, Material.FLOWER_POT);
        put(Material.POTTED_CRIMSON_FUNGUS, Material.FLOWER_POT);
        put(Material.POTTED_CRIMSON_ROOTS, Material.FLOWER_POT);
        put(Material.POTTED_DANDELION, Material.FLOWER_POT);
        put(Material.POTTED_DARK_OAK_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_FERN, Material.FLOWER_POT);
        put(Material.POTTED_FLOWERING_AZALEA_BUSH, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_JUNGLE_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_LILY_OF_THE_VALLEY, Material.FLOWER_POT);
        put(Material.POTTED_MANGROVE_PROPAGULE, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_OAK_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_OPEN_EYEBLOSSOM, Material.FLOWER_POT);
        put(Material.POTTED_ORANGE_TULIP, Material.FLOWER_POT);
        put(Material.POTTED_OXEYE_DAISY, Material.FLOWER_POT);
        put(Material.POTTED_PALE_OAK_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_PINK_TULIP, Material.FLOWER_POT);
        put(Material.POTTED_POPPY, Material.FLOWER_POT);
        put(Material.POTTED_RED_MUSHROOM, Material.FLOWER_POT);
        put(Material.POTTED_RED_TULIP, Material.FLOWER_POT);
        put(Material.POTTED_SPRUCE_SAPLING, Material.POTTED_DEAD_BUSH);
        put(Material.POTTED_TORCHFLOWER, Material.FLOWER_POT);
        put(Material.POTTED_WARPED_FUNGUS, Material.FLOWER_POT);
        put(Material.POTTED_WARPED_ROOTS, Material.FLOWER_POT);
        put(Material.POTTED_WITHER_ROSE, Material.FLOWER_POT);
        put(Material.RED_MUSHROOM, Material.AIR);
        put(Material.RED_MUSHROOM_BLOCK, Material.AIR);
        put(Material.RED_TULIP, Material.AIR);
        put(Material.ROSE_BUSH, Material.DEAD_BUSH);
        put(Material.SHORT_DRY_GRASS, Material.AIR);
        put(Material.SHORT_GRASS, Material.AIR);
        put(Material.SMALL_DRIPLEAF, Material.AIR);
        put(Material.SPORE_BLOSSOM, Material.AIR);
        put(Material.SPRUCE_LEAVES, Material.AIR);
        put(Material.SUNFLOWER, Material.AIR);
        put(Material.SWEET_BERRY_BUSH, Material.DEAD_BUSH);
        put(Material.TALL_DRY_GRASS, Material.AIR);
        put(Material.TALL_GRASS, Material.AIR);
        put(Material.TORCHFLOWER, Material.AIR);
        put(Material.VINE, Material.AIR);
        put(Material.WARPED_FUNGUS, Material.AIR);
        put(Material.WARPED_STEM, Material.AIR);
        put(Material.WEEPING_VINES, Material.AIR);
        put(Material.WEEPING_VINES_PLANT, Material.AIR);
        put(Material.WHITE_TULIP, Material.AIR);
        put(Material.WILDFLOWERS, Material.AIR);
        put(Material.WITHER_ROSE, Material.AIR);
    }};

    /**
     * Mapping of vegetation materials to the items they drop when destroyed.
     *
     * <p>Saplings drop sticks, bamboo drops bamboo items. The block itself is then removed.</p>
     */
    Map<Material, Material> blocksToItemReplacements = new HashMap<>() {{
        put(Material.ACACIA_SAPLING, Material.STICK);
        put(Material.BAMBOO, Material.BAMBOO);
        put(Material.BIRCH_SAPLING, Material.STICK);
        put(Material.CHERRY_SAPLING, Material.STICK);
        put(Material.DARK_OAK_SAPLING, Material.STICK);
        put(Material.JUNGLE_SAPLING, Material.STICK);
        put(Material.OAK_SAPLING, Material.STICK);
        put(Material.PALE_OAK_SAPLING, Material.STICK);
        put(Material.SPRUCE_SAPLING, Material.STICK);
    }};

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public HERBICIDE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.HERBICIDE;
    }

    /**
     * Constructs a new HERBICIDE spell cast by a player.
     *
     * <p>Creates a herbicide field at the specified location with the given radius and duration.
     * All plant life within the radius will be destroyed or transformed into dead alternatives.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public HERBICIDE(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.HERBICIDE;

        setRadius(radius);
        setDuration(duration);

        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        // check that world guard permissions allow this stationary spell at every location in the spell radius
        checkWorldGuard();
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-20 blocks) and duration boundaries (30 seconds to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Ages the spell and eliminates plant life within the protected area.
     *
     * <p>Every 10 ticks, processes all blocks in the spell radius:</p>
     * <ul>
     *   <li>Replaces plant blocks with dead alternatives (leaves→air, grass→dirt, etc.)</li>
     *   <li>Drops items for saplings/bamboo and removes the block</li>
     *   <li>Resets crop age to 0 for immature crops</li>
     * </ul>
     */
    @Override
    public void upkeep() {
        if (duration % 10 == 0) {
            // kill any plant blocks in the radius
            for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius)) {
                // replace plant blocks with "dead" alternatives
                Material blockType = block.getType();
                if (blocksReplacements.containsKey(blockType)) {
                    block.setType(blocksReplacements.get(blockType));
                    common.printDebugMessage("HERBICIDE: changing " + blockType + " to " + block.getType(), null, null, false);
                    continue;
                }
                else if (blocksToItemReplacements.containsKey(blockType)) {
                    World world = block.getLocation().getWorld();
                    if (world == null) {
                        common.printDebugMessage("HERBICIDE: null world", null, null, true);
                        continue;
                    }

                    Item item = world.dropItemNaturally(block.getLocation(), new ItemStack(blocksToItemReplacements.get(blockType)));
                    common.printDebugMessage("HERBICIDE: replacing " + blockType + " with " + item.getItemStack().getType(), null, null, false);
                    block.setType(Material.AIR);
                    continue;
                }

                // kill crops
                BlockData blockData = block.getBlockData();
                if (blockData instanceof Ageable) {
                    ((Ageable) blockData).setAge(0);
                    block.setBlockData(blockData);
                }
            }
        }

        age();
    }

    /**
     * Serializes the herbicide spell data for persistence.
     *
     * <p>The herbicide spell has no extra data to serialize beyond the base spell properties,
     * so this method returns an empty map.</p>
     *
     * @return an empty map (the spell has no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes herbicide spell data from saved state.
     *
     * <p>The herbicide spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the herbicide spell ends.
     *
     * <p>The herbicide spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}
