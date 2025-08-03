package net.pottercraft.ollivanders2.stationaryspell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HERBICIDE extends ThrownPotionStationarySpell {
    Map<Material, Material> blocksReplacements = new HashMap<>() {{
        put(Material.ACACIA_LEAVES, Material.LEAF_LITTER);
        put(Material.ACACIA_SAPLING, Material.STICK);
        put(Material.ALLIUM, Material.AIR);
        put(Material.AZALEA, Material.DEAD_BUSH);
        put(Material.AZALEA_LEAVES, Material.LEAF_LITTER);
        put(Material.AZURE_BLUET, Material.AIR);
        put(Material.BIG_DRIPLEAF, Material.LEAF_LITTER);
        put(Material.BIG_DRIPLEAF_STEM, Material.AIR);
        put(Material.BIRCH_LEAVES, Material.LEAF_LITTER);
        put(Material.BIRCH_SAPLING, Material.STICK);
        put(Material.BLUE_ORCHID, Material.AIR);
        put(Material.BROWN_MUSHROOM, Material.AIR);
        put(Material.CAVE_VINES_PLANT, Material.AIR);
        put(Material.CHERRY_LEAVES, Material.LEAF_LITTER);
        put(Material.CHERRY_SAPLING, Material.STICK);
        put(Material.CHORUS_PLANT, Material.DEAD_BUSH);
        put(Material.CHORUS_FLOWER, Material.AIR);
        put(Material.CORNFLOWER, Material.AIR);
        put(Material.DANDELION, Material.AIR);
        put(Material.DARK_OAK_LEAVES, Material.LEAF_LITTER);
        put(Material.DARK_OAK_SAPLING, Material.STICK);
        put(Material.FERN, Material.LEAF_LITTER);
        put(Material.FLOWERING_AZALEA, Material.DEAD_BUSH);
        put(Material.FLOWERING_AZALEA_LEAVES, Material.LEAF_LITTER);
        put(Material.GLOW_LICHEN, Material.AIR);
        put(Material.GRASS_BLOCK, Material.DIRT);
        put(Material.JUNGLE_LEAVES, Material.LEAF_LITTER);
        put(Material.JUNGLE_SAPLING, Material.STICK);
        put(Material.LARGE_FERN, Material.LEAF_LITTER);
        put(Material.LILAC, Material.AIR);
        put(Material.LILY_OF_THE_VALLEY, Material.AIR);
        put(Material.LILY_PAD, Material.AIR);
        put(Material.MANGROVE_LEAVES, Material.LEAF_LITTER);
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
        put(Material.OAK_LEAVES, Material.LEAF_LITTER);
        put(Material.OAK_SAPLING, Material.STICK);
        put(Material.ORANGE_TULIP, Material.AIR);
        put(Material.OXEYE_DAISY, Material.AIR);
        put(Material.PALE_HANGING_MOSS, Material.AIR);
        put(Material.PALE_MOSS_BLOCK, Material.DIRT);
        put(Material.PALE_MOSS_CARPET, Material.DIRT);
        put(Material.PALE_OAK_LEAVES, Material.LEAF_LITTER);
        put(Material.PALE_OAK_SAPLING, Material.STICK);
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
        put(Material.SPRUCE_LEAVES, Material.LEAF_LITTER);
        put(Material.SPRUCE_SAPLING, Material.STICK);
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
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public HERBICIDE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.HERBICIDE;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
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

        if (!isKilled()) // possible if the world guard check failed
           common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Age the spell by 1 tick
     */
    @Override
    public void upkeep() {
        age();

        // kill any plant blocks in the radius
        for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius)) {
            // kill crops
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Ageable) {
                ((Ageable)blockData).setAge(0);
                block.setBlockData(blockData);
                continue;
            }

            // replace other plant blocks with "dead" alternatives
            Material blockType = block.getType();
            if (blocksReplacements.containsKey(blockType)) {
                block.setType(blocksReplacements.get(blockType));
            }
        }
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    @Override
    void doCleanUp() {
    }
}
