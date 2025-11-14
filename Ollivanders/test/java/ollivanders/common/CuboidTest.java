package ollivanders.common;

import net.pottercraft.ollivanders2.common.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Cuboid class.
 * Tests cuboid area validation, location checking, and parsing functionality.
 */
public class CuboidTest {
    static ServerMock mockServer;
    static final String worldName = "world";
    static World testWorld;
    static int[] cuboidArea = {0, 0, 0, 100, 100, 100};
    static Cuboid testCuboid;

    /**
     * Set up the mock server, test world, and test cuboid before all tests.
     */
    @BeforeAll
    static void globalSetUp () {
        mockServer = MockBukkit.mock();
        testWorld = mockServer.addSimpleWorld(worldName);

        testCuboid = new Cuboid(worldName, cuboidArea);
    }

    /**
     * Test that a location inside the cuboid is correctly identified as inside.
     */
    @Test
    void isInsideTest() {
        assertTrue(testCuboid.isInside(new Location(testWorld, 5, 5, 5)), "testCuboid.isInside(new Location(testWorld, 5, 5, 5) is false, expected true");
    }

    /**
     * Test that a location in a different world is not considered inside the cuboid,
     * even if the coordinates are within the cuboid's bounds.
     */
    @Test
    void IsInsideWrongWorldTest() {
        World anotherWorld = mockServer.addSimpleWorld("nether");
        assertFalse(testCuboid.isInside(new Location (anotherWorld, 5, 5, 5)), "testCuboid.isInside(new Location (anotherWorld, 5, 5, 5) is true, expected false");
    }

    /**
     * Test that a location with coordinates outside the cuboid's bounds is correctly
     * identified as outside.
     */
    @Test
    void isInsideCoordsOutsideTest() {
        assertFalse(testCuboid.isInside(new Location(testWorld, 200, 200, 200)), "testCuboid.isInside(new Location(testWorld, 200, 200, 200) true, expected false");
    }

    /**
     * Test that parseArea correctly converts a space-delimited string of coordinates
     * into an integer array representing a cuboid area.
     * Expected format: "x1 y1 z1 x2 y2 z2"
     */
    @Test
    void parseAreaTest() {
        // make an area array from a string of two x, y, z coordinates in the format "0 0 0 0 0 0"
        int[] area = Cuboid.parseArea("0 0 0 100 100 100");
        assertNotNull(area, "Cuboid.parseArea() created null area");
        assertArrayEquals(area, cuboidArea, "Cuboid.parseArea() did not create expected area");
    }

    /**
     * Test that parseArea returns null when given malformed input strings,
     * including non-numeric strings and strings with incorrect delimiters.
     */
    @Test
    void parseAreaMalformattedStringTest() {
        int[] area = Cuboid.parseArea("this is not a numerical string");
        assertNull(area, "Cuboid.parseArea() did not return null when given a non-numeric string");

        area = Cuboid.parseArea("0, 0, 0, 0, 0, 0");
        assertNull(area, "Cuboid.parseArea() did not return null when given a comma-delimited string");
    }

    /**
     * Test that parseArea returns null when given a string with fewer than 6 numbers.
     * A valid cuboid area requires exactly 6 coordinates.
     */
    @Test
    void parseAreaTooFewNumbersTest() {
        int[] area = Cuboid.parseArea("1 2 3 4 5");
        assertNull(area, "Cuboid.parseArea() did not return null when given too few numbers");
    }

    /**
     * Test that parseArea returns null when given a string with more than 6 numbers.
     * A valid cuboid area requires exactly 6 coordinates.
     */
    @Test
    void parseAreaTooManyNumbersTest() {
        int[] area = Cuboid.parseArea("1 2 3 4 5 6 7");
        assertNull(area, "Cuboid.parseArea() did not return null when given too many numbers");
    }

    /**
     * Clean up the mock server after all tests have run.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
