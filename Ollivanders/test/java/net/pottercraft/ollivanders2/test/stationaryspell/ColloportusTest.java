package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link COLLOPORTUS} stationary spell.
 *
 * <p>Tests the colloportus locking spell, which prevents doors, trapdoors, and chests from being
 * opened, broken, or interacted with within the spell's protected area. Inherits common spell tests
 * from {@link O2StationarySpellTest} and provides spell-specific factory methods for test setup.</p>
 *
 * <p>The test verifies:
 * <ul>
 *   <li>Blocks within the spell area cannot be broken</li>
 *   <li>Doors within the spell area cannot be broken by entities</li>
 *   <li>Blocks within the spell area cannot be changed by entities</li>
 *   <li>Entities cannot interact with blocks within the spell area</li>
 *   <li>Players cannot interact with blocks within the spell area</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 */
public class ColloportusTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#COLLOPORTUS}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.COLLOPORTUS;
    }

    /**
     * Creates a COLLOPORTUS spell instance for testing.
     *
     * <p>Constructs a new colloportus spell at the specified location with the fixed 5-block radius.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new COLLOPORTUS spell instance (not null)
     */
    @Override
    COLLOPORTUS createStationarySpell(Player caster, Location location) {
        return new COLLOPORTUS(testPlugin, caster.getUniqueId(), location);
    }

    /**
     * Creates a test door block at the specified location.
     *
     * <p>Sets an acacia door block at the given location for testing block break and interaction events.</p>
     *
     * @param testWorld the Bukkit world to create the door in (not null)
     * @param location  the location where the door should be created (not null)
     * @return the door block that was created (not null)
     */
    Block makeDoor(World testWorld, Location location) {
        Block doorBlock = testWorld.getBlockAt(location);
        doorBlock.setType(Material.ACACIA_DOOR);

        return doorBlock;
    }

    /**
     * Tests colloportus upkeep behavior (skipped - covered by base class tests).
     *
     * <p>The colloportus spell's upkeep method has no special actions, only the inherited aging logic
     * which is already tested comprehensively by the base test class.</p>
     */
    @Override @Test
    void upkeepTest() {
        // colloportus upkeep has no actions
    }

    /**
     * Tests block break event cancellation within the spell's protected area.
     *
     * <p>Verifies that when a player attempts to break a block within the colloportus spell's radius,
     * the BlockBreakEvent is cancelled, preventing the block from being broken.</p>
     */
    @Test
    void doOnBlockBreakEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        Block door = makeDoor(testWorld, location);
        PlayerMock player = mockServer.addPlayer();

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        mockServer.getScheduler().performTicks(20);

        BlockBreakEvent event = new BlockBreakEvent(door, player);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "BlockBreakEvent not cancelled by Colloportus");

        colloportus.kill();
    }

    /**
     * Tests entity door break event cancellation within the spell's protected area.
     *
     * <p>Verifies that when an entity (such as a player or monster) attempts to break down a door
     * within the colloportus spell's radius, the EntityBreakDoorEvent is cancelled, preventing the
     * door from being broken.</p>
     */
    @Test
    void doOnEntityBreakDoorEvent() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 4, 100);
        Block door = makeDoor(testWorld, location);
        PlayerMock player = mockServer.addPlayer();

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        mockServer.getScheduler().performTicks(20);

        EntityBreakDoorEvent event = new EntityBreakDoorEvent(player, door, door.getBlockData());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "EntityBreakDoorEvent not cancelled by Colloportus");
    }

    /**
     * Tests entity block change event cancellation within the spell's protected area.
     *
     * <p>Verifies that when an entity (such as an enderman) attempts to change a block within the
     * colloportus spell's radius, the EntityChangeBlockEvent is cancelled, preventing the block
     * from being modified.</p>
     */
    @Test
    void doOnEntityChangeBlockEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 4, 100);
        Block door = makeDoor(testWorld, location);
        PlayerMock player = mockServer.addPlayer();

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        mockServer.getScheduler().performTicks(20);

        EntityChangeBlockEvent event = new EntityChangeBlockEvent(player, door, door.getBlockData());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "EntityChangeBlockEvent not cancelled by Colloportus");
    }

    /**
     * Tests entity interact event cancellation within the spell's protected area.
     *
     * <p>Verifies that when an entity attempts to interact with a block within the colloportus spell's
     * radius, the EntityInteractEvent is cancelled, preventing interaction with the block.</p>
     */
    @Test
    void doOnEntityInteractEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 4, 100);
        Block door = makeDoor(testWorld, location);
        PlayerMock player = mockServer.addPlayer();

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        mockServer.getScheduler().performTicks(20);

        EntityInteractEvent event = new EntityInteractEvent(player, door);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "EntityInteractEvent not cancelled by Colloportus");
    }

    /**
     * Tests player interact event denial within the spell's protected area.
     *
     * <p>Verifies that when a player attempts to interact with a block within the colloportus spell's
     * radius, the PlayerInteractEvent result is set to DENY, preventing the player from opening or
     * manipulating the block.</p>
     */
    @Test
    void doOnPlayerInteractEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 600, 4, 100);
        Block door = makeDoor(testWorld, location);
        PlayerMock player = mockServer.addPlayer();

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        mockServer.getScheduler().performTicks(20);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, null, door, BlockFace.EAST);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertEquals(Event.Result.DENY, event.useInteractedBlock(), "PlayerInteractEvent not canceled by Colloportus");
    }
}
