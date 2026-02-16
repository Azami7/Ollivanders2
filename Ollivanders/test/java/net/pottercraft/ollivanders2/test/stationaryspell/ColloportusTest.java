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

public class ColloportusTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.COLLOPORTUS;
    }

    @Override
    COLLOPORTUS createStationarySpell(Player caster, Location location) {
        return new COLLOPORTUS(testPlugin, caster.getUniqueId(), location);
    }

    Block makeDoor(World testWorld, Location location) {
        Block doorBlock = testWorld.getBlockAt(location);
        doorBlock.setType(Material.ACACIA_DOOR);

        return doorBlock;
    }

    @Override @Test
    void upkeepTest() {
        // colloportus upkeep has no actions
    }

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
