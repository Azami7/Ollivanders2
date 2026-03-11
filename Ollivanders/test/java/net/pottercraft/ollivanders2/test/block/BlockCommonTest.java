package net.pottercraft.ollivanders2.test.block;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockCommonTest {
    static ServerMock mockServer;
    static Ollivanders2 testPlugin;
    static Ollivanders2Common o2common;
    Location origin;
    World testWorld;
    final String worldName = "world";

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        o2common = new Ollivanders2Common(testPlugin);
    }

    @BeforeEach
    void setUp() {
        testWorld = mockServer.addSimpleWorld(worldName);
        origin = new Location(testWorld, 0, 4, 0);
    }

    /**
     * Tests identifying blocks adjacent in all six cardinal directions.
     * Verifies that blocks one unit away in any direction are recognized as adjacent.
     */
    @Test
    void isAdjacentToTest() {
        // create a center block
        Block centerBlock = testWorld.getBlockAt(new Location(testWorld, 100, 5, 100));
        centerBlock.setType(Material.STONE);

        // test adjacent ABOVE (UP)
        Block aboveBlock = testWorld.getBlockAt(new Location(testWorld, 100, 6, 100));
        aboveBlock.setType(Material.DIRT);
        assertTrue(BlockCommon.isAdjacentTo(centerBlock, aboveBlock), "Block above should be adjacent");

        // test adjacent BELOW (DOWN)
        Block belowBlock = testWorld.getBlockAt(new Location(testWorld, 100, 4, 100));
        belowBlock.setType(Material.DIRT);
        assertTrue(BlockCommon.isAdjacentTo(centerBlock, belowBlock), "Block below should be adjacent");

        // test adjacent to the NORTH
        Block northBlock = testWorld.getBlockAt(new Location(testWorld, 100, 5, 99));
        northBlock.setType(Material.DIRT);
        assertTrue(BlockCommon.isAdjacentTo(centerBlock, northBlock), "Block to the north should be adjacent");

        // test adjacent to the SOUTH
        Block southBlock = testWorld.getBlockAt(new Location(testWorld, 100, 5, 101));
        southBlock.setType(Material.DIRT);
        assertTrue(BlockCommon.isAdjacentTo(centerBlock, southBlock), "Block to the south should be adjacent");

        // test adjacent to the EAST
        Block eastBlock = testWorld.getBlockAt(new Location(testWorld, 101, 5, 100));
        eastBlock.setType(Material.DIRT);
        assertTrue(BlockCommon.isAdjacentTo(centerBlock, eastBlock), "Block to the east should be adjacent");

        // test adjacent to the WEST
        Block westBlock = testWorld.getBlockAt(new Location(testWorld, 99, 5, 100));
        westBlock.setType(Material.DIRT);
        assertTrue(BlockCommon.isAdjacentTo(centerBlock, westBlock), "Block to the west should be adjacent");
    }

    /**
     * Tests that non-adjacent blocks are not identified as adjacent.
     * Verifies that diagonal, distant, and far blocks are correctly distinguished from adjacent blocks.
     */
    @Test
    void isAdjacentToNotAdjacentTest() {
        // create a center block
        Block centerBlock = testWorld.getBlockAt(new Location(testWorld, 200, 5, 100));
        centerBlock.setType(Material.STONE);

        // test diagonal block (not adjacent)
        Block diagonalBlock = testWorld.getBlockAt(new Location(testWorld, 201, 5, 101));
        diagonalBlock.setType(Material.DIRT);
        assertFalse(BlockCommon.isAdjacentTo(centerBlock, diagonalBlock), "Diagonal block should not be adjacent");

        // test block 2 units away horizontally (not adjacent)
        Block twoAwayBlock = testWorld.getBlockAt(new Location(testWorld, 200, 5, 102));
        twoAwayBlock.setType(Material.DIRT);
        assertFalse(BlockCommon.isAdjacentTo(centerBlock, twoAwayBlock), "Block 2 units away should not be adjacent");

        // test block 2 units away vertically (not adjacent)
        Block twoUpBlock = testWorld.getBlockAt(new Location(testWorld, 200, 7, 100));
        twoUpBlock.setType(Material.DIRT);
        assertFalse(BlockCommon.isAdjacentTo(centerBlock, twoUpBlock), "Block 2 units above should not be adjacent");
    }

    /**
     * Tests that a block is not adjacent to itself.
     * Verifies that the same block instance is not considered adjacent to itself.
     */
    @Test
    void isAdjacentToSameBlockTest() {
        // test that a block is not adjacent to itself
        Block block = testWorld.getBlockAt(new Location(testWorld, 300, 5, 100));
        block.setType(Material.STONE);

        assertFalse(BlockCommon.isAdjacentTo(block, block), "A block should not be adjacent to itself");
    }

    /**
     * Tests retrieving all blocks within a given radius.
     * Verifies that a radius of 2 returns a 3x3x3 cube of 27 blocks.
     */
    @Test
    void getBlocksInRadiusTest() {
        List<Block> blocks = BlockCommon.getBlocksInRadius(origin, 2);
        // expect 3^3 blocks since radius of 2 is not inclusive of the origin block
        assertEquals(27, blocks.size());
    }

    @Test
    void getBlocksInRadiusByTypeTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 400, 5, 100));
        block.setType(Material.STONE);
        block.getRelative(BlockFace.EAST).setType(Material.STONE);

        List<Block> blocks = BlockCommon.getBlocksInRadiusByType(block.getLocation(), 5, Material.FIRE);
        assertTrue(blocks.isEmpty(), "getBlocksInRadiusByType() not empty when no blocks of that type are nearby");

        blocks = BlockCommon.getBlocksInRadiusByType(block.getLocation(), 5, Material.AIR);
        assertFalse(blocks.isEmpty(), "getBlocksInRadiusByType() empty when blocks of type are nearby");

        blocks = BlockCommon.getBlocksInRadiusByType(block.getLocation(), 5, Material.STONE);
        assertEquals(2, blocks.size(), "getBlocksInRadiusByType() returned unexpected count of matching blocks");
    }

    /**
     * Test isAirBlock returns expected values for air and non-air materials
     */
    @Test
    void isAirBlockTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 500, 5, 100));
        block.setType(Material.STONE);

        assertFalse(BlockCommon.isAirBlock(block), "isAirBlock() returned true for Material.STONE");

        block.setType(Material.AIR);
        assertTrue(BlockCommon.isAirBlock(block), "isAirBlock() returned false for Material.AIR");
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
