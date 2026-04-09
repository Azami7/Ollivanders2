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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link COLLOPORTUS} spell, which locks doors and trapdoors.
 *
 * <p>Verifies the spell creates a stationary lock enchantment on doors and fails on non-door blocks.</p>
 *
 * @author Azami7
 * @see COLLOPORTUS
 * @see net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS
 */
public class ColloportusTest extends StationarySpellTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.COLLOPORTUS;
    }

    /** {@inheritDoc} */
    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.COLLOPORTUS;
    }

    /**
     * Sets up an oak door at the target location for the spell to lock.
     *
     * @param location the target location
     */
    @Override
    void targetLocationSetup(Location location) {
        World world = location.getWorld();
        assertNotNull(world);

        TestCommon.createBlockBase(location.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3);
        world.getBlockAt(location).setType(Material.OAK_DOOR);
    }

    /**
     * Verifies that COLLOPORTUS fails on non-door blocks and successfully locks open doors.
     *
     * <p>Tests:</p>
     * <ul>
     * <li>Casting at a sign block fails and sends a failure message</li>
     * <li>Casting at a closed door fails</li>
     * <li>Casting at an open door locks it and creates a stationary spell</li>
     * </ul>
     */
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
        assertTrue(colloportus.hasHitBlock());
        assertTrue(colloportus.isKilled());

        blockData = testWorld.getBlockAt(targetLocation).getBlockData();
        assertInstanceOf(Openable.class, blockData);
        assertFalse(((Openable) blockData).isOpen(), "Door not set to closed when colloportus activated");
    }
}
