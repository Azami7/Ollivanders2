package net.pottercraft.ollivanders2.test.common;

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
     * Test that a point inside the x and y range but with z below the cuboid's z range
     * is correctly identified as outside.
     *
     * <p>This case exercises the z-axis check independently from the x-axis check.
     * A previous bug in {@link net.pottercraft.ollivanders2.common.Cuboid#isInside(String, int, int, int)}
     * used the x coordinate where the z coordinate should have been used in the z-range
     * comparison, so any test point that disagreed between x-inside and z-inside would
     * have surfaced it. Points where x and z move together (e.g. (5,5,5) or (200,200,200))
     * could not distinguish the bug from correct behavior.
     */
    @Test
    void isInsideZBelowRangeTest() {
        // x and y are inside [0,100] but z = -50 is below z1 = 0
        assertFalse(testCuboid.isInside(new Location(testWorld, 50, 50, -50)),
                "testCuboid.isInside(50, 50, -50) returned true; expected false because z is below the cuboid's z range");
    }

    /**
     * Test that a point inside the x and y range but with z above the cuboid's z range
     * is correctly identified as outside. Companion to {@link #isInsideZBelowRangeTest()}.
     */
    @Test
    void isInsideZAboveRangeTest() {
        // x and y are inside [0,100] but z = 200 is above z2 = 100
        assertFalse(testCuboid.isInside(new Location(testWorld, 50, 50, 200)),
                "testCuboid.isInside(50, 50, 200) returned true; expected false because z is above the cuboid's z range");
    }

    /**
     * Test that a cuboid defined with reversed corners (corner1 coordinates greater than
     * corner2 coordinates on every axis) still classifies points correctly.
     *
     * <p>The {@code isInside} implementation has separate branches for {@code n1 > n2}
     * and {@code n1 < n2} on each axis; this test exercises the reversed-corner branches
     * that the canonical {@code testCuboid} (with corner1 = origin, corner2 = (100,100,100))
     * does not cover.
     */
    @Test
    void isInsideReversedCornersTest() {
        // same physical region as testCuboid but with the corner order swapped on every axis
        Cuboid reversedCuboid = new Cuboid(worldName, new int[]{100, 100, 100, 0, 0, 0});

        assertTrue(reversedCuboid.isInside(new Location(testWorld, 50, 50, 50)),
                "reversedCuboid should contain (50,50,50)");
        assertFalse(reversedCuboid.isInside(new Location(testWorld, 50, 50, -50)),
                "reversedCuboid should not contain (50,50,-50) — z below range");
        assertFalse(reversedCuboid.isInside(new Location(testWorld, 200, 200, 200)),
                "reversedCuboid should not contain (200,200,200) — outside on all axes");
    }

    /**
     * Test that points exactly on the cuboid's corner coordinates are considered inside.
     *
     * <p>The cuboid bounds are documented as inclusive, so both corners and any point
     * coinciding with a face must report as inside.
     */
    @Test
    void isInsideBoundaryTest() {
        assertTrue(testCuboid.isInside(new Location(testWorld, 0, 0, 0)),
                "corner (0,0,0) should be inside the cuboid");
        assertTrue(testCuboid.isInside(new Location(testWorld, 100, 100, 100)),
                "corner (100,100,100) should be inside the cuboid");
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
