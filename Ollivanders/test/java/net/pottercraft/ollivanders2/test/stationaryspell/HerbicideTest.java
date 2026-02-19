package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.HERBICIDE;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the HERBICIDE stationary spell.
 *
 * <p>Tests spell-specific behavior for the plant destruction effect that eliminates all plant
 * life within the protected area. Inherits common stationary spell tests from the base class
 * and adds tests for the plant killing mechanics including block replacement, item drops,
 * and crop aging.</p>
 *
 * @author Azami7
 */
public class HerbicideTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#HERBICIDE}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.HERBICIDE;
    }

    /**
     * Creates a HERBICIDE spell instance for testing.
     *
     * <p>Constructs a new spell at the specified location with the minimum radius and duration values.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new HERBICIDE spell instance (not null)
     */
    @Override
    HERBICIDE createStationarySpell(Player caster, Location location) {
        return new HERBICIDE(testPlugin, caster.getUniqueId(), location, HERBICIDE.minRadiusConfig, HERBICIDE.minDurationConfig);
    }

    /**
     * Tests the upkeep behavior and plant destruction mechanics.
     *
     * <p>Verifies that the spell correctly:
     * <ul>
     *   <li>Replaces plant blocks with dead alternatives (flowers to air, mossy blocks to non-mossy)</li>
     *   <li>Drops items for saplings and bamboo</li>
     *   <li>Resets crop age to 0</li>
     *   <li>Leaves non-plant blocks unaffected (snow)</li>
     *   <li>Does not affect blocks outside the spell radius</li>
     * </ul>
     * </p>
     */
    @Override @Test
    void upkeepTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + HERBICIDE.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        Block plant1 = testWorld.getBlockAt(location);
        plant1.setType(Material.ORANGE_TULIP); // should turn in to AIR
        Block plant2 = plant1.getRelative(BlockFace.EAST);
        plant2.setType(Material.MOSSY_STONE_BRICK_WALL); // should turn in to brick wall
        Block plant3 = plant1.getRelative(BlockFace.WEST);
        plant3.setType(Material.ACACIA_SAPLING); // should turn in to a stick

        Block crop = plant1.getRelative(BlockFace.DOWN);
        crop.setType(Material.WHEAT);

        Block nonPlant = plant1.getRelative(BlockFace.NORTH);
        nonPlant.setType(Material.SNOW);

        Block plant5 = testWorld.getBlockAt(outsideLocation);
        plant5.setType(Material.ORANGE_TULIP);

        HERBICIDE herbicide = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(herbicide);
        mockServer.getScheduler().performTicks(200);

        // plant blocks inside spell area are changed
        assertTrue(herbicide.isLocationInside(plant1.getLocation()));
        assertEquals(Material.AIR, plant1.getType(), "Tulip plant not expected material");
        assertTrue(herbicide.isLocationInside(plant2.getLocation()));
        assertEquals(Material.STONE_BRICK_WALL, plant2.getType(), "Mossy stone brick wall not expected material");

        assertTrue(herbicide.isLocationInside(plant3.getLocation()));
        assertEquals(Material.AIR, plant3.getType(), "Acacia sapling not removed");
        List<Item> items = TestCommon.getAllItems(testWorld);
        assertFalse(items.isEmpty(), "acacia sapling not replaced by item");
        assertEquals(Material.STICK, items.getFirst().getItemStack().getType(), "Acacia sapling item not expected type");

        // clean up item
        items.getFirst().remove();
        mockServer.getScheduler().performTicks(20);

        // add bamboo plant, which also turns in to an item
        Block plant4 = plant1.getRelative(BlockFace.SOUTH);
        plant4.setType(Material.BAMBOO); // should turn into a bamboo stick

        mockServer.getScheduler().performTicks(200);
        assertTrue(herbicide.isLocationInside(plant4.getLocation()));
        items = TestCommon.getAllItems(testWorld);
        assertFalse(items.isEmpty(), "bamboo shoot not replaced by item");
        assertEquals(Material.BAMBOO, items.getFirst().getItemStack().getType(), "bamboo item not expected type");

        // crop blocks killed
        assertTrue(herbicide.isLocationInside(crop.getLocation()));
        assertEquals(Material.WHEAT, crop.getType(), "crop type changed");
        BlockData cropData = crop.getBlockData();
        assertInstanceOf(Ageable.class, cropData);
        assertEquals(0, ((Ageable)cropData).getAge(), "crop age not set to 0");

        // non-plant blocks inside spell area are unaffected
        assertTrue(herbicide.isLocationInside(nonPlant.getLocation()));
        assertEquals(Material.SNOW, nonPlant.getType(), "Snow block affected");

        // plant blocks outside spell area are unaffected
        assertFalse(herbicide.isLocationInside(plant5.getLocation()));
        assertEquals(Material.ORANGE_TULIP, plant5.getType(), "tulip outside spell area affected");

        // age() is tested by ageAndKillTest
    }
}
