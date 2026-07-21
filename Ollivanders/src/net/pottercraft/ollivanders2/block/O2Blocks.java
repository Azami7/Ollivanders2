package net.pottercraft.ollivanders2.block;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
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
     * Blocks temporarily changed by magic, keyed to their original data and the magic that changed them.
     */
    private final Map<Block, ChangedBlockData> temporarilyChangedBlocks = new HashMap<>();

    private final Ollivanders2 p;

    /**
     * Container for tracking a changed block's original state and the spell that changed it.
     */
    private static class ChangedBlockData {
        /** Original block data before the spell changed it */
        BlockData blockData;
        /** The spell that changed this block */
        O2Spell changedBy;
        /** The stationary spell that changed this block */
        O2StationarySpell changedByStationary;

        /**
         * @param data            the original block data
         * @param spell           the spell that changed the block, or null if a stationary spell did
         * @param stationarySpell the stationary spell that changed the block, or null if a spell did
         */
        ChangedBlockData(@NotNull BlockData data, @Nullable O2Spell spell, @Nullable O2StationarySpell stationarySpell) {
            blockData = data;
            changedBy = spell;
            changedByStationary = stationarySpell;
        }
    }

    /**
     * Constructor.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public O2Blocks(@NotNull Ollivanders2 plugin) {
        p = plugin;

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Plugin enable hook; no startup work is needed.
     */
    public void onEnable() {
    }

    /**
     * Track a block before a spell changes it so its original state can be restored later. Call this before modifying
     * the block. Do not use for permanent changes meant to survive the magic ending or a server restart.
     *
     * @param block     the block, captured at its current pre-change state
     * @param changedBy the spell changing the block
     * @return true if the block is now tracked; false if it was already tracked
     */
    public synchronized boolean addTemporarilyChangedBlock(@NotNull Block block, @NotNull O2Spell changedBy) {
        if (temporarilyChangedBlocks.containsKey(block))
            return false;

        BlockData blockData = block.getBlockData();
        if (blockData == null)
            return false;

        temporarilyChangedBlocks.put(block, new ChangedBlockData(blockData, changedBy, null));
        return true;
    }

    /**
     * Track a block before a stationary spell changes it so its original state can be restored later. Call this before
     * modifying the block. Do not use for permanent changes meant to survive the magic ending or a server restart.
     *
     * @param block     the block, captured at its current pre-change state
     * @param changedBy the stationary spell changing the block
     * @return true if the block is now tracked; false if it was already tracked
     */
    public synchronized boolean addTemporarilyChangedBlock(@NotNull Block block, @NotNull O2StationarySpell changedBy) {
        if (temporarilyChangedBlocks.containsKey(block))
            return false;

        BlockData blockData = block.getBlockData();
        if (blockData == null)
            return false;

        temporarilyChangedBlocks.put(block, new ChangedBlockData(blockData, null, changedBy));
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
     * @return the blocks changed by the spell; empty if none
     */
    @NotNull
    public List<Block> getBlocksChangedBySpell(@NotNull O2Spell spell) {
        ArrayList<Block> changed = new ArrayList<>();

        for (Block block : temporarilyChangedBlocks.keySet()) {
            ChangedBlockData changedBlockData = temporarilyChangedBlocks.get(block);

            if (changedBlockData.changedBy != null && changedBlockData.changedBy.equals(spell)) {
                changed.add(block);
            }
        }

        return changed;
    }

    /**
     * Get all blocks that were changed by a specific stationary spell.
     *
     * @param spell the stationary spell to query
     * @return the blocks changed by the stationary spell; empty if none
     */
    @NotNull
    public List<Block> getBlocksChangedByStationarySpell(@NotNull O2StationarySpell spell) {
        ArrayList<Block> changed = new ArrayList<>();

        for (Block block : temporarilyChangedBlocks.keySet()) {
            ChangedBlockData changedBlockData = temporarilyChangedBlocks.get(block);

            if (changedBlockData.changedByStationary != null && changedBlockData.changedByStationary.equals(spell)) {
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
     * Get the stationary spell that changed a block.
     *
     * @param block the block to query
     * @return the stationary spell that changed the block, or null if not tracked
     */
    @Nullable
    public O2StationarySpell getChangedByStationary(@NotNull Block block) {
        if (!temporarilyChangedBlocks.containsKey(block))
            return null;

        return temporarilyChangedBlocks.get(block).changedByStationary;
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
     * Revert all blocks that were changed by a specific stationary spell.
     *
     * @param spell the stationary spell whose blocks should be reverted
     */
    public void revertTemporarilyChangedBlocksBy(@NotNull O2StationarySpell spell) {
        List<Block> blocks = getBlocksChangedByStationarySpell(spell);

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
        Set<Block> blocks = new HashSet<>() {{
            addAll(temporarilyChangedBlocks.keySet());
        }};

        for (Block block : blocks) {
            revertTemporarilyChangedBlock(block);
        }

        temporarilyChangedBlocks.clear();
    }
}
