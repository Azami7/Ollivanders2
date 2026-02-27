package net.pottercraft.ollivanders2.block;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages global block state and temporary block changes made by spells.
 *
 * <p>Tracks blocks that have been temporarily changed by spells, storing their original block data.
 * When spells are killed or the plugin disables, all tracked blocks are reverted to their original state.
 * Prevents drops from blocks broken while under temporary spell effects.</p>
 *
 * @author Azami7
 */
public final class O2Blocks implements Listener {
    /**
     * temporarily changed blocks and their original blockState
     */
    private final Map<Block, ChangedBlockData> temporarilyChangedBlocks = new HashMap<>();

    /**
     * Reference to the Ollivanders2 plugin instance
     */
    private final Ollivanders2 p;

    /**
     * Utility class for common operations and message printing
     */
    private final Ollivanders2Common common;

    /**
     * Container for tracking a changed block's original state and the spell that changed it.
     */
    private class ChangedBlockData {
        /** Original block data before the spell changed it */
        BlockData blockData;
        /** The spell that changed this block */
        O2Spell changedBy;

        /**
         * Constructor.
         *
         * @param data  the original block data
         * @param spell the spell that changed the block
         */
        ChangedBlockData(@NotNull BlockData data, @NotNull O2Spell spell) {
            blockData = data;
            changedBy = spell;
        }
    }

    /**
     * Constructor.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public O2Blocks(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     *
     */
    public void onEnable() {
    }

    /**
     * Add a block *before* it has been changed. This should not be used for blocks that should not be reverted when the
     * magic that created them is killed or the server is restarted.
     *
     * @param block     the block
     * @param changedBy the spell that changed the block
     */
    public synchronized boolean addTemporarilyChangedBlock(@NotNull Block block, @NotNull O2Spell changedBy) {
        if (temporarilyChangedBlocks.containsKey(block))
            return false;

        BlockData blockData = block.getBlockData();
        if (blockData == null)
            return false;

        temporarilyChangedBlocks.put(block, new ChangedBlockData(blockData, changedBy));
        return true;
    }

    /**
     * Check if a block is being tracked as temporarily changed.
     *
     * @param block the block to check
     * @return true if the block is temporarily changed, false otherwise
     */
    public boolean isTemporarilyChangedBlock(@NotNull Block block) {
        return temporarilyChangedBlocks.containsKey(block);
    }

    /**
     * Get all blocks that were changed by a specific spell.
     *
     * @param spell the spell to query
     * @return list of blocks changed by the spell
     */
    @NotNull
    public List<Block> getBlocksChangedBySpell(@NotNull O2Spell spell) {
        ArrayList<Block> changed = new ArrayList<>();

        for (Block block : temporarilyChangedBlocks.keySet()) {
            ChangedBlockData changedBlockData = temporarilyChangedBlocks.get(block);

            if (changedBlockData.changedBy.equals(spell)) {
                changed.add(block);
            }
        }

        return changed;
    }

    /**
     * Get the spell that changed a block.
     *
     * @param block the block to query
     * @return the spell that changed the block, or null if not tracked
     */
    @Nullable
    public O2Spell getChangedBy(@NotNull Block block) {
        if (!temporarilyChangedBlocks.containsKey(block))
            return null;

        return temporarilyChangedBlocks.get(block).changedBy;
    }

    /**
     * Revert a list of temporarily changed blocks to their original state.
     *
     * @param blocks the list of blocks to revert
     */
    public void revertTemporarilyChangedBlocks(@NotNull List<Block> blocks) {
        for (Block block : blocks) {
            if (block != null)
                revertTemporarilyChangedBlock(block);
        }
    }

    /**
     * Revert all blocks that were changed by a specific spell.
     *
     * @param spell the spell whose blocks should be reverted
     */
    public void revertTemporarilyChangedBlocksBy(@NotNull O2Spell spell) {
        List<Block> blocks = getBlocksChangedBySpell(spell);

        for (Block block : blocks) {
            revertTemporarilyChangedBlock(block);
        }
    }

    /**
     * Revert a single temporarily changed block to its original state.
     *
     * @param block the block to revert
     */
    public synchronized void revertTemporarilyChangedBlock(@NotNull Block block) {
        if (!isTemporarilyChangedBlock(block))
            return;

        BlockData originalBlockData = temporarilyChangedBlocks.get(block).blockData;

        if (originalBlockData != null) {
            block.setBlockData(originalBlockData);
        }

        temporarilyChangedBlocks.remove(block);
    }

    /**
     * If a block is broken that is temporary, prevent it from dropping anything.
     *
     * @param event the block break event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTemporaryBlockBreak(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();

        if (isTemporarilyChangedBlock(block)) {
            event.setDropItems(false);
        }
    }

    /**
     * Revert any temporarily changed blocks that are still changed.
     */
    public synchronized void onDisable() {
        Set<Block> blocks = new HashSet<>();
        blocks.addAll(temporarilyChangedBlocks.keySet());

        for (Block block : blocks) {
            revertTemporarilyChangedBlock(block);
        }

        temporarilyChangedBlocks.clear();
    }
}
