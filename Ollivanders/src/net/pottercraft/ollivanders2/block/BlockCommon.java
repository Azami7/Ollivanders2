package net.pottercraft.ollivanders2.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for common block functions.
 *
 * @author Azami7
 */
public class BlockCommon {
    /**
     * Check if a block is adjacent to another block
     *
     * @param block1 the first block
     * @param block2 the second block
     * @return true if the blocks are adjacent, false otherwise
     */
    static public boolean isAdjacentTo(@NotNull Block block1, @NotNull Block block2) {
        // Check all six faces
        return block1.getRelative(BlockFace.UP).equals(block2) ||
                block1.getRelative(BlockFace.DOWN).equals(block2) ||
                block1.getRelative(BlockFace.NORTH).equals(block2) ||
                block1.getRelative(BlockFace.SOUTH).equals(block2) ||
                block1.getRelative(BlockFace.EAST).equals(block2) ||
                block1.getRelative(BlockFace.WEST).equals(block2);
    }

    /**
     * Gets the blocks in a radius of a location.
     *
     * @param loc    The Location that is the center of the block list
     * @param radius The radius of the block list
     * @return List of blocks that are within radius of the location.
     */
    @NotNull
    static public List<Block> getBlocksInRadius(@NotNull Location loc, double radius) {
        Block center = loc.getBlock();
        int blockRadius = (int) (radius);
        List<Block> blockList = new ArrayList<>();
        for (int x = -blockRadius; x <= blockRadius; x++) {
            for (int y = -blockRadius; y <= blockRadius; y++) {
                for (int z = -blockRadius; z <= blockRadius; z++) {
                    Block block = center.getRelative(x, y, z);
                    if (block.getLocation().distance(center.getLocation()) < radius && !blockList.contains(block)) { // make sure we aee still in the radius since we're rounding a square
                        blockList.add(block);
                    }
                }
            }
        }

        return blockList;
    }

    /**
     * Gets the blocks in a radius of a location that are a specific type.
     *
     * @param location  The Location that is the center of the block list
     * @param radius    The radius of the block list
     * @param blockType The material type of block to find
     * @return List of blocks that are within radius of the location and are the specified type.
     */
    @NotNull
    static public List<Block> getBlocksInRadiusByType(@NotNull Location location, double radius, @NotNull Material blockType) {
        List<Block> blockList = getBlocksInRadius(location, radius);
        List<Block> matchingBlocks = new ArrayList<>();

        for (Block block : blockList) {
            if (block.getType() == blockType)
                matchingBlocks.add(block);
        }

        return matchingBlocks;
    }

    /**
     * Is this block an air block?
     *
     * <p>Moving this to a common function since there are now multiple air blocks.</p>
     *
     * @param block the block to check
     * @return true if it is an air block, false otherwise
     */
    public static boolean isAirBlock(@NotNull Block block) {
        return block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR;
    }
}
