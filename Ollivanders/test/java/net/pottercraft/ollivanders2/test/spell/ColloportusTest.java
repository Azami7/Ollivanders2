package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.COLLOPORTUS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Isolated
public class ColloportusTest extends StationarySpellTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.COLLOPORTUS;
    }

    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.COLLOPORTUS;
    }

    @Override
    void targetLocationSetup(Location location) {
        World world = location.getWorld();
        assertNotNull(world);

        TestCommon.createBlockBase(location.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3);
        world.getBlockAt(location).setType(Material.OAK_DOOR);
    }

    @Test
    void createStationarySpellTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3);
        testWorld.getBlockAt(targetLocation).setType(Material.OAK_SIGN);

        COLLOPORTUS colloportus = (COLLOPORTUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(colloportus.isKilled());

        assertTrue(Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(targetLocation, getStationarySpellType()).isEmpty(), "Stationary spell unexpectedly created");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message");
        assertEquals(colloportus.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster received unexpected failure message");

        testWorld.getBlockAt(targetLocation).setType(Material.OAK_DOOR);
        BlockData blockData = testWorld.getBlockAt(targetLocation).getBlockData();
        assertInstanceOf(Openable.class, blockData);
        ((Openable) blockData).setOpen(true);
        testWorld.getBlockAt(targetLocation).setBlockData(blockData);

        colloportus = (COLLOPORTUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(colloportus.hasHitTarget());
        assertTrue(colloportus.isKilled());

        blockData = testWorld.getBlockAt(targetLocation).getBlockData();
        assertInstanceOf(Openable.class, blockData);
        assertFalse(((Openable) blockData).isOpen(), "Door not set to closed when colloportus activated");
    }
}
