package net.pottercraft.ollivanders2.test.block;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.O2Blocks;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for O2Blocks functionality.
 *
 * <p>Tests block tracking, reversion, and query methods. Uses a single test method to avoid
 * parallel execution interference since all tests share the same O2Blocks instance.</p>
 *
 * @author Azami7
 */
public class O2BlocksTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Tests all O2Blocks functionality in a single method to avoid parallel test interference.
     *
     * <p>Verifies:</p>
     * <ul>
     * <li>Adding temporarily changed blocks</li>
     * <li>Duplicate block add returns false</li>
     * <li>Querying changed blocks by spell</li>
     * <li>Getting the spell that changed a block</li>
     * <li>Reverting a single block restores original material</li>
     * <li>Reverting all blocks changed by a spell</li>
     * <li>Querying untracked blocks returns null/empty</li>
     * <li>onDisable reverts all remaining blocks</li>
     * </ul>
     */
    @Test
    void temporarilyChangedBlocksTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        O2Blocks o2Blocks = Ollivanders2API.getBlocks();
        assertNotNull(o2Blocks);

        // create two spells to use as changedBy references
        PlayerMock caster = mockServer.addPlayer();
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        caster.setLocation(TestCommon.faceTarget(location, targetLocation));

        O2Spell spell1 = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.DEPULSO, O2PlayerCommon.rightWand);
        assertNotNull(spell1, "Failed to create spell1");
        O2Spell spell2 = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.DEPULSO, O2PlayerCommon.rightWand);
        assertNotNull(spell2, "Failed to create spell2");

        // set up blocks
        Block block1 = new Location(testWorld, 200, 40, 200).getBlock();
        block1.setType(Material.STONE);
        Block block2 = new Location(testWorld, 201, 40, 200).getBlock();
        block2.setType(Material.OAK_LOG);
        Block block3 = new Location(testWorld, 202, 40, 200).getBlock();
        block3.setType(Material.DIRT);
        Block untrackedBlock = new Location(testWorld, 203, 40, 200).getBlock();
        untrackedBlock.setType(Material.SAND);

        // -- test untracked block queries --
        assertFalse(o2Blocks.isTemporarilyChangedBlock(untrackedBlock), "untracked block should not be tracked");
        assertNull(o2Blocks.getChangedBy(untrackedBlock), "untracked block should not have a spell");
        assertTrue(o2Blocks.getBlocksChangedBySpell(spell1).isEmpty(), "no blocks should be tracked yet");

        // -- test adding blocks --
        assertTrue(o2Blocks.addTemporarilyChangedBlock(block1, spell1), "should add block1");
        assertTrue(o2Blocks.addTemporarilyChangedBlock(block2, spell1), "should add block2");
        assertTrue(o2Blocks.addTemporarilyChangedBlock(block3, spell2), "should add block3");

        // -- test duplicate add returns false --
        assertFalse(o2Blocks.addTemporarilyChangedBlock(block1, spell1), "duplicate add should return false");
        assertFalse(o2Blocks.addTemporarilyChangedBlock(block1, spell2), "duplicate add with different spell should return false");

        // -- test isTemporarilyChangedBlock --
        assertTrue(o2Blocks.isTemporarilyChangedBlock(block1), "block1 should be tracked");
        assertTrue(o2Blocks.isTemporarilyChangedBlock(block2), "block2 should be tracked");
        assertTrue(o2Blocks.isTemporarilyChangedBlock(block3), "block3 should be tracked");

        // -- test getChangedBy --
        assertEquals(spell1, o2Blocks.getChangedBy(block1), "block1 changed by spell1");
        assertEquals(spell1, o2Blocks.getChangedBy(block2), "block2 changed by spell1");
        assertEquals(spell2, o2Blocks.getChangedBy(block3), "block3 changed by spell2");

        // -- test getBlocksChangedBySpell --
        List<Block> spell1Blocks = o2Blocks.getBlocksChangedBySpell(spell1);
        assertEquals(2, spell1Blocks.size(), "spell1 should have changed 2 blocks");
        assertTrue(spell1Blocks.contains(block1), "spell1 blocks should contain block1");
        assertTrue(spell1Blocks.contains(block2), "spell1 blocks should contain block2");

        List<Block> spell2Blocks = o2Blocks.getBlocksChangedBySpell(spell2);
        assertEquals(1, spell2Blocks.size(), "spell2 should have changed 1 block");
        assertTrue(spell2Blocks.contains(block3), "spell2 blocks should contain block3");

        // -- test revert single block restores original material --
        block1.setType(Material.DIAMOND_BLOCK); // simulate spell changing the block
        o2Blocks.revertTemporarilyChangedBlock(block1);
        assertEquals(Material.STONE, block1.getType(), "block1 should be reverted to STONE");
        assertFalse(o2Blocks.isTemporarilyChangedBlock(block1), "block1 should no longer be tracked");

        // -- test revert untracked block does nothing --
        o2Blocks.revertTemporarilyChangedBlock(untrackedBlock); // should not throw

        // -- test revertTemporarilyChangedBlocksBy reverts all blocks for a spell --
        block2.setType(Material.GOLD_BLOCK); // simulate spell changing block2
        block3.setType(Material.IRON_BLOCK); // simulate spell changing block3

        // add block1 back for spell1 revert test
        block1.setType(Material.COBBLESTONE);
        assertTrue(o2Blocks.addTemporarilyChangedBlock(block1, spell1), "should re-add block1");
        block1.setType(Material.EMERALD_BLOCK); // simulate spell changing it

        o2Blocks.revertTemporarilyChangedBlocksBy(spell1);
        assertEquals(Material.COBBLESTONE, block1.getType(), "block1 should be reverted to COBBLESTONE");
        assertEquals(Material.OAK_LOG, block2.getType(), "block2 should be reverted to OAK_LOG");
        assertFalse(o2Blocks.isTemporarilyChangedBlock(block1), "block1 should no longer be tracked");
        assertFalse(o2Blocks.isTemporarilyChangedBlock(block2), "block2 should no longer be tracked");

        // block3 was changed by spell2 so should still be tracked
        assertTrue(o2Blocks.isTemporarilyChangedBlock(block3), "block3 should still be tracked");

        // -- test revertTemporarilyChangedBlocks with a list of blocks --
        // re-add blocks for this test
        block1.setType(Material.STONE);
        assertTrue(o2Blocks.addTemporarilyChangedBlock(block1, spell1), "should re-add block1");
        block1.setType(Material.DIAMOND_BLOCK);

        block2.setType(Material.OAK_LOG);
        assertTrue(o2Blocks.addTemporarilyChangedBlock(block2, spell2), "should re-add block2");
        block2.setType(Material.GOLD_BLOCK);

        // revert a list containing both blocks (from different spells)
        List<Block> blocksToRevert = List.of(block1, block2);
        o2Blocks.revertTemporarilyChangedBlocks(blocksToRevert);
        assertEquals(Material.STONE, block1.getType(), "block1 should be reverted to STONE");
        assertEquals(Material.OAK_LOG, block2.getType(), "block2 should be reverted to OAK_LOG");
        assertFalse(o2Blocks.isTemporarilyChangedBlock(block1), "block1 should no longer be tracked");
        assertFalse(o2Blocks.isTemporarilyChangedBlock(block2), "block2 should no longer be tracked");

        // block3 should still be tracked (not in the revert list)
        assertTrue(o2Blocks.isTemporarilyChangedBlock(block3), "block3 should still be tracked");

        // -- test revertTemporarilyChangedBlocks with untracked blocks in list does nothing --
        o2Blocks.revertTemporarilyChangedBlocks(List.of(untrackedBlock)); // should not throw

        // -- test onDisable reverts all remaining blocks --
        o2Blocks.onDisable();
        assertEquals(Material.DIRT, block3.getType(), "block3 should be reverted to DIRT after onDisable");
        assertFalse(o2Blocks.isTemporarilyChangedBlock(block3), "block3 should no longer be tracked after onDisable");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}