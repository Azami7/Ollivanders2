package net.pottercraft.ollivanders2.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Class for common block functions.
 *
 * @author Azami7
 */
public class BlockCommon {
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
