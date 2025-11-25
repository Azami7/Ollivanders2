package net.pottercraft.ollivanders2.test.common;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Ollivanders2Common utility class.
 * Tests cover string parsing, location serialization/deserialization, player interactions,
 * message broadcasting, block operations, and coordinate conversions.
 */
public class Ollivanders2CommonTest {
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
     * Tests parsing a valid UUID string to a UUID object.
     * Verifies that a correctly formatted UUID string is successfully converted.
     */
    @Test
    void uuidFromStringTest() {
        String uuidString = "5d2bec8f-2402-4039-920b-bdbf348fa9a7";
        UUID uuid = o2common.uuidFromString(uuidString);
        assertNotNull(uuid, "o2common.uuidFromString() returned null for " + uuidString);
    }

    /**
     * Tests parsing an invalid UUID string.
     * Verifies that parsing an invalid string returns null.
     */
    @Test
    void uuidFromStringBadStringTest() {
        String uuidBadString = "badstring";
        UUID uuid = o2common.uuidFromString(uuidBadString);
        assertNull(uuid, "o2common.uuidFromString(uuidBadString) did not return null");
    }

    /**
     * Tests parsing a valid integer string to an Integer object.
     * Verifies that a correctly formatted numeric string is successfully converted.
     */
    @Test
    void integerFromStringTest() {
        String integerString = "12345";
        Integer integer = o2common.integerFromString(integerString);
        assertNotNull(integer, "o2common.integerFromString() returned null for " + integerString);
    }

    /**
     * Tests parsing an invalid integer string.
     * Verifies that parsing a non-numeric string returns null.
     */
    @Test
    void integerFromStringBadStringTest() {
        String integerBadString = "badstring";
        Integer integer = o2common.integerFromString(integerBadString);
        assertNull(integer, "o2common.integerFromString(uuidBadString) did not return null");
    }

    /**
     * Tests parsing a valid boolean string to a Boolean object.
     * Verifies that a "true" string is successfully converted to a Boolean.
     */
    @Test
    void booleanFromStringTest() {
        String boolString = "true";
        Boolean bool = o2common.booleanFromString(boolString);
        assertNotNull(bool, "o2common.booleanFromString() returned null for " + boolString);
    }

    /**
     * Tests parsing an invalid boolean string.
     * Verifies that parsing a non-boolean string returns false.
     */
    @Test
    void booleanFromStringBadStringTest() {
        String boolBadString = "badstring";
        Boolean bool = o2common.booleanFromString(boolBadString);
        assertFalse(bool, "o2common.booleanFromString(boolBadString) did not return false");
    }

    /**
     * Tests checking if a location is within a radius of another location.
     * Verifies that a location 5 blocks away is within a 10 block radius.
     */
    @Test
    void isInsideTest() {
        Location checkLocation = new Location(testWorld, 5, 4, 0);

        assertTrue(Ollivanders2Common.isInside(origin, checkLocation, 10));
    }

    /**
     * Tests checking if a location is outside a radius.
     * Verifies that a location 15 blocks away is outside a 10 block radius.
     */
    @Test
    void isInsideOutsideTest() {
        Location checkLocation = new Location(testWorld, 15, 4, 0);

        assertFalse(Ollivanders2Common.isInside(origin, checkLocation, 10));
    }

    /**
     * Tests retrieving all blocks within a given radius.
     * Verifies that a radius of 2 returns a 3x3x3 cube of 27 blocks.
     */
    @Test
    void getBlocksInRadiusTest() {
        List<Block> blocks = Ollivanders2Common.getBlocksInRadius(origin, 2);
        // expect 3^3 blocks since radius of 2 is not inclusive of the origin block
        assertEquals(27, blocks.size());
    }

    /**
     * Tests converting enum names to human-readable format.
     * Verifies that enum constants like "FUMOS_DUO" are converted to "fumos duo".
     */
    @Test
    void enumRecodeTest() {
        String sourceString = "FUMOS_DUO";
        String expectedString = "fumos duo";
        String enumString = Ollivanders2Common.enumRecode(sourceString);

        assertEquals(expectedString, enumString, "Ollivanders2Common.enumRecode(sourceString) Expected: " + expectedString + " Actual: " + enumString);
    }

    /**
     * Tests capitalizing the first letter of each word in a string.
     * Verifies that "fumos duo" is converted to "Fumos Duo".
     */
    @Test
    void firstLetterCapitalizeTest() {
         String sourceString = "fumos duo";
         String expectedString = "Fumos Duo";
         String capitalizedString = Ollivanders2Common.firstLetterCapitalize(sourceString);

         assertEquals(expectedString, capitalizedString, "Ollivanders2Common.firstLetterCapitalize(sourceString) Expected: " + expectedString + ", Actual: " + capitalizedString);
    }

    /**
     * Tests serializing a Location to a Map of string key-value pairs.
     * Verifies that location coordinates and world name are correctly mapped with a given label prefix.
     */
    @Test
    void serializeLocationTest() {
        String labelPrefix = "test";

        Map<String, String> actual = o2common.serializeLocation(origin, labelPrefix);
        assertNotNull(actual, "o2common.serializeLocation(origin, labelPrefix) returned null");
        assertTrue(actual.containsKey(labelPrefix + "_" + Ollivanders2Common.locationWorldLabel), "o2common.serializeLocation() does not contain " + labelPrefix + "_" + Ollivanders2Common.locationWorldLabel);
        assertEquals(worldName, actual.get(labelPrefix + "_" + Ollivanders2Common.locationWorldLabel), "");
    }

    /**
     * Tests serializing a Location with a null world.
     * Verifies that locations with null worlds are rejected and return null.
     */
    @Test
    void serializeLocationBadLocationTest() {
        Location badLocation = new Location(null, 0, 4, 0);
        Map<String, String> actual = o2common.serializeLocation(badLocation, "test");
        assertNull(actual, "o2common.serializeLocation(badLocation, \"test\") did not return null");
    }

    /**
     * Tests deserializing a Location from a Map of string key-value pairs.
     * Verifies that a serialized location can be reconstructed with correct world name and coordinates.
     */
    @Test
    void deserializeLocationTest() {
        String labelPrefix = "test";
        Map<String, String> serializedLocation = o2common.serializeLocation(origin, labelPrefix);
        assertNotNull(serializedLocation, "o2common.serializeLocation(origin, labelPrefix) returned null");

        Location actual = o2common.deserializeLocation(serializedLocation, labelPrefix);
        assertNotNull(actual, "o2common.deserializeLocation(serializedLocation, labelPrefix) returned null");
        World actualWorld = actual.getWorld();
        assertNotNull(actualWorld, "actual.getWorld() was null");
        assertEquals(worldName, actualWorld.getName(), "World name did not match, Expected: " + worldName + ", Actual: " + actualWorld.getName());
    }

    /**
     * Tests deserializing a Location from a Map with missing or invalid coordinates.
     * Verifies that deserialization with null or missing coordinates returns null.
     */
    @Test
    void deserializeLocationNullTest() {
        Map<String, String> badLocation = new HashMap<>() {{
            put("test" + "_" + Ollivanders2Common.locationWorldLabel, "badWorld");
            put("test" + "_" + Ollivanders2Common.locationXLabel, null);
            put("test" + "_" + Ollivanders2Common.locationYLabel, "4");
            put("test" + "_" + Ollivanders2Common.locationZLabel, "0");
        }};

        Location actual = o2common.deserializeLocation(badLocation, "test");
        assertNull(actual, "o2common.deserializeLocation(badLocation, \"test\") with null X coord did not return null");

        badLocation.remove("test" + "_" + Ollivanders2Common.locationXLabel);
        actual = o2common.deserializeLocation(badLocation, "test");
        assertNull(actual, "o2common.deserializeLocation(badLocation, \"test\") with missing X coord did not return null");
    }

    /**
     * Tests checking if a player is facing a specific block type.
     * Verifies that the correct block is returned when a player faces a matching block type.
     */
    @Test
    void playerFacingBlockTypeTest() {
        // get the block at origin
        Block block = testWorld.getBlockAt(origin);

        // check if the player is facing a block of type stone
        Block facingBlock = playerFacingBlockTypeHelper(Material.STONE, Material.STONE);
        assertNotNull(facingBlock, "Ollivanders2Common.playerFacingBlockType() should return a block when facing STONE");
        assertEquals(block, facingBlock, "Should return the block at origin");
    }

    /**
     * Tests checking for a specific block type when player faces a different block.
     * Verifies that null is returned when the faced block type doesn't match the requested type.
     */
    @Test
    void playerFacingBlockTypeFailureTest() {
        // check if they are facing a block of type stone but actually make the type wood
        Block facingBlock = playerFacingBlockTypeHelper(Material.DARK_OAK_WOOD, Material.STONE);
        assertNull(facingBlock, "Ollivanders2Common.playerFacingBlockType() should return null facing WOOD");
    }

    /**
     * Tests checking for a block type when the player is facing air.
     * Verifies that null is returned when the player is not facing any blocks of the requested type.
     */
    @Test
    void playerFacingBlockTypeNothingTest() {
        // check if the player is facing a block of type stone (there should be none in that direction)
        Block facingBlock = playerFacingBlockTypeHelper(Material.AIR, Material.STONE);
        assertNull(facingBlock, "Ollivanders2Common.playerFacingBlockType() should return null when not facing the block type");
    }

    Block playerFacingBlockTypeHelper(@NotNull Material originBlockType, @NotNull Material checkBlockType) {
        // make the block at origin type STONE
        Block block = testWorld.getBlockAt(origin);
        block.setType(originBlockType);

        // create a player and have them stand 3 blocks away facing the block at origin
        PlayerMock player = mockServer.addPlayer();
        Location playerLocation = new Location(testWorld, 0, 4, 3);
        player.setLocation(playerLocation);

        // set the player's rotation to face the block at origin (yaw=180 to face negative Z, pitch=0 for horizontal)
        player.setRotation(180, 0);

        // advance the game ticks 5 for everything to settle
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);

        // check if the player is facing a block of type stone
        return Ollivanders2Common.playerFacingBlockType(player, checkBlockType);
    }

    /**
     * Tests converting spherical coordinates to 3D vectors.
     * Verifies that spherical coordinates are correctly converted with proper magnitude and direction.
     * Tests both north pole and equator coordinates.
     */
    @Test
    void sphereToVectorTest() {
        // test conversion of spherical coordinates to vector
        double radius = 10;

        // test north pole (inclination = 0, azimuth = 0)
        Vector northPole = Ollivanders2Common.sphereToVector(new double[]{0, 0}, (int) radius);
        assertNotNull(northPole, "sphereToVector() should return a vector");
        assertEquals(radius, northPole.length(), 0.01, "Vector magnitude should equal radius");

        // test equator (inclination = π/2, azimuth = 0)
        Vector equator = Ollivanders2Common.sphereToVector(new double[]{Math.PI / 2, 0}, (int) radius);
        assertNotNull(equator, "sphereToVector() should return a vector");
        assertEquals(radius, equator.length(), 0.01, "Vector magnitude should equal radius");
    }

    /**
     * There is no effective way to test flair() since we cannot access the particle system to see if the effect worked
     * as intended.
     */
    @Test
    void flairTest() {
        Ollivanders2Common.flair(new Location(testWorld, 200, 4, 200), 5, 3);
        mockServer.getScheduler().performTicks(20);

        Ollivanders2Common.flair(new Location(testWorld, 300, 4, 300), 5, 3, Particle.SMOKE);
        mockServer.getScheduler().performTicks(20);

        Ollivanders2Common.flair(null, 5, 5);
        mockServer.getScheduler().performTicks(10);
    }

    /**
     * Tests sending title and subtitle messages to multiple players.
     * Verifies that all players in the list receive the correct title and subtitle text.
     */
    @Test
    void sendTitleMessageTest() {
        // create multiple mock players
        PlayerMock player1 = mockServer.addPlayer("Fred");
        PlayerMock player2 = mockServer.addPlayer("Joe");

        // add players to a list
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // call sendTitleMessage with title and subtitle
        String title = "Test Title";
        String subtitle = "Test Subtitle";
        Ollivanders2Common.sendTitleMessage("Test Title", "Test Subtitle", players);
        mockServer.getScheduler().performTicks(10);

        String receivedTitle = player1.nextTitle();
        String recievedSubtitle = player1.nextSubTitle();
        assertNotNull(receivedTitle, "player1.nextTitle() was null");
        assertNotNull(recievedSubtitle, "player1.nextSubTitle() was null");
        assertEquals(title, receivedTitle, "Player1 did not receive title, expected: " + title + ", actual: " + receivedTitle);
        assertEquals(subtitle, recievedSubtitle, "Player1 did not receive subtitle, expected: " + subtitle + ", actual: " + recievedSubtitle);

        receivedTitle = player2.nextTitle();
        recievedSubtitle = player2.nextSubTitle();
        assertNotNull(receivedTitle, "player2.nextTitle() was null");
        assertNotNull(recievedSubtitle, "player2.nextSubTitle() was null");
        assertEquals(title, receivedTitle, "Player2 did not receive title, expected: " + title + ", actual: " + receivedTitle);
        assertEquals(subtitle, recievedSubtitle, "Player2 did not receive subtitle, expected: " + subtitle + ", actual: " + recievedSubtitle);
    }

    /**
     * Tests sending messages to players within a given radius.
     * Verifies that players within the radius receive the message while those outside do not.
     */
    @Test
    void sendMessageInRadius() {
        // create multiple mock players
        PlayerMock player1 = mockServer.addPlayer();
        PlayerMock player2 = mockServer.addPlayer();
        PlayerMock player3 = mockServer.addPlayer();

        player1.setLocation(new Location(testWorld, 200, 4, 100));
        player2.setLocation(new Location(testWorld, 200, 4, 102));
        player3.setLocation(new Location(testWorld, 200, 4, 107));
        mockServer.getScheduler().performTicks(5);

        String testMessage = "testMessage";
        Ollivanders2Common.sendMessageInRadius(testMessage, new Location(testWorld, 200, 4, 100), 5);
        mockServer.getScheduler().performTicks(5);

        String received = player1.nextMessage();
        assertNotNull(received, "player1.nextMessage() was null");
        assertTrue(TestCommon.messageEquals(testMessage, received), "Player1 did not receive correct message, expected: " + testMessage + ", actual: " + received);

        received = player2.nextMessage();
        assertNotNull(received, "player2.nextMessage() was null");
        assertTrue(TestCommon.messageEquals(testMessage, received), "Player2 did not receive correct message, expected: " + testMessage + ", actual: " + received);

        received = player3.nextMessage();
        assertNull(received, "player3.nextMessage() was not null");
    }

    @NotNull
    Handler logHandlerHelper(@NotNull List<LogRecord> logRecords) {
        return new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRecords.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
    }

    /**
     * Tests printing debug messages when debug mode is enabled.
     * Verifies that debug messages are logged when Ollivanders2.debug is true.
     */
    @Test
    void printDebugMessageTest() {
        List<LogRecord> logRecords = new ArrayList<>();
        Handler testLogHandler = logHandlerHelper(logRecords);
        testPlugin.getLogger().addHandler(testLogHandler);

        String debugMessage = "debug message";
        Ollivanders2.debug = true;
        o2common.printDebugMessage(debugMessage, null, null, false);
        Ollivanders2.debug = false;

        assertEquals(1, logRecords.size(), "logRecords.size() did not increase");
        String received = logRecords.getFirst().getMessage();
        assertTrue(received.contains(debugMessage), "Did not find log message, expected: " + debugMessage + ", actual: " + received);
    }

    /**
     * Tests that debug messages are suppressed when debug mode is disabled.
     * Verifies that no debug messages are logged when Ollivanders2.debug is false.
     */
    @Test
    void printDebugMessageNoDebugTest() {
        List<LogRecord> logRecords = new ArrayList<>();
        Handler testLogHandler = logHandlerHelper(logRecords);
        testPlugin.getLogger().addHandler(testLogHandler);

        String debugMessage = "no debug message";
        o2common.printDebugMessage(debugMessage, null, null, false);

        assertEquals(0, logRecords.size(), "logRecords.size() changed when Ollivanders2Common.printDebugMessage() called but Ollivanders2.debug false");
    }

    /**
     * Tests printing debug messages as warnings.
     * Verifies that debug messages can be logged with a WARNING level when flagged.
     */
    @Test
    void printDebugMessageWarningTest() {
        List<LogRecord> logRecords = new ArrayList<>();
        Handler testLogHandler = logHandlerHelper(logRecords);
        testPlugin.getLogger().addHandler(testLogHandler);

        Ollivanders2.debug = true;
        o2common.printDebugMessage("warning debug message", null, null, true);
        Ollivanders2.debug = false;

        assertEquals(1, logRecords.size(), "logRecords.size() did not increase");
        Level received = logRecords.getFirst().getLevel();
        assertSame(Level.WARNING, received, "Did not find warning log message, expected: " + Level.WARNING + ", actual: " + received);
    }

    /**
     * Tests logging messages at INFO level.
     * Verifies that log messages are properly recorded regardless of debug mode.
     */
    @Test
    void printLogMessage() {
        List<LogRecord> logRecords = new ArrayList<>();
        Handler testLogHandler = logHandlerHelper(logRecords);
        testPlugin.getLogger().addHandler(testLogHandler);

        String logMessage = "log message";
        o2common.printLogMessage(logMessage, null, null, false);

        assertEquals(1, logRecords.size(), "logRecords.size() did not increase");
        String received = logRecords.getFirst().getMessage();
        assertTrue(received.contains(logMessage), "Did not get log message, expected: " + logMessage + ", actual: " + received);
    }

    /**
     * Tests logging messages as warnings.
     * Verifies that log messages can be recorded with a WARNING level when flagged.
     */
    @Test
    void printLogWarningMessage() {
        List<LogRecord> logRecords = new ArrayList<>();
        Handler testLogHandler = logHandlerHelper(logRecords);
        testPlugin.getLogger().addHandler(testLogHandler);

        o2common.printLogMessage("warning log message", null, null, true);

        assertEquals(1, logRecords.size(), "logRecords.size() did not increase");
        Level received = logRecords.getFirst().getLevel();
        assertSame(Level.WARNING, received, "Did not find warning log message, expected: " + Level.WARNING + ", actual: " + received);
    }

    /**
     * Tests comparing two identical locations for equality.
     * Verifies that identical locations are recognized as equal.
     */
    @Test
    void locationEqualsTest() {
        assertTrue(Ollivanders2Common.locationEquals(origin, origin), "Ollivanders2Common.locationEquals(origin, origin) did not return true");

        Location loc1 = new Location(testWorld, 5, 5, 5);
        Location loc2 = new Location(testWorld, 5, 5, 5);
        assertTrue(Ollivanders2Common.locationEquals(loc1, loc2), "Ollivanders2Common.locationEquals(loc1, loc2) did not return true when locations identical");
    }

    /**
     * Tests comparing different locations for inequality.
     * Verifies that locations with different coordinates or worlds are recognized as unequal.
     */
    @Test
    void locationEqualsFalseTest() {
        Location loc1 = new Location(testWorld, 5, 5, 5);
        Location loc2 = new Location(testWorld, 6, 5, 5);
        assertFalse(Ollivanders2Common.locationEquals(loc1, loc2), "Ollivanders2Common.locationEquals() was not false when location X coords different");

        World nether = mockServer.addSimpleWorld("nether");
        loc2 = new Location(nether, 5, 5, 5);
        assertFalse(Ollivanders2Common.locationEquals(loc1, loc2), "Ollivanders2Common.locationEquals() was not false when world are different");
    }

    /**
     * Tests filtering recipients by distance with a standard radius.
     * Verifies that players within the radius are kept and those outside are removed.
     */
    @Test
    void chatDropoffTest() {
        // create players at different distances from origin
        PlayerMock player1 = mockServer.addPlayer("Close1");
        player1.setLocation(new Location(testWorld, 2, 4, 0));  // 2 blocks away

        PlayerMock player2 = mockServer.addPlayer("Close2");
        player2.setLocation(new Location(testWorld, 0, 4, 3));  // 3 blocks away

        PlayerMock player3 = mockServer.addPlayer("Far1");
        player3.setLocation(new Location(testWorld, 10, 4, 0));  // 10 blocks away

        PlayerMock player4 = mockServer.addPlayer("Far2");
        player4.setLocation(new Location(testWorld, 0, 4, 15));  // 15 blocks away

        // create recipients set with all players
        Set<Player> recipients = new HashSet<>();
        recipients.add(player1);
        recipients.add(player2);
        recipients.add(player3);
        recipients.add(player4);

        // test with dropoff of 5 blocks - should only keep close players
        Ollivanders2Common.chatDropoff(recipients, 5, origin);

        // Verify close players are still in the set
        assertTrue(recipients.contains(player1), "Player at 2 blocks should be within 5 block dropoff");
        assertTrue(recipients.contains(player2), "Player at 3 blocks should be within 5 block dropoff");

        // Verify far players were removed
        assertFalse(recipients.contains(player3), "Player at 10 blocks should be removed from 5 block dropoff");
        assertFalse(recipients.contains(player4), "Player at 15 blocks should be removed from 5 block dropoff");

        assertEquals(2, recipients.size(), "Expected 2 recipients remaining after 5 block dropoff");
    }

    /**
     * Tests chat dropoff with a large radius.
     * Verifies that all players are kept when the radius is sufficiently large.
     */
    @Test
    void chatDropoffLargeRadiusTest() {
        // create players at various distances
        PlayerMock player1 = mockServer.addPlayer("PlayA");
        player1.setLocation(new Location(testWorld, 5, 4, 5));  // ~7 blocks away

        PlayerMock player2 = mockServer.addPlayer("PlayB");
        player2.setLocation(new Location(testWorld, 10, 4, 10));  // ~14 blocks away

        PlayerMock player3 = mockServer.addPlayer("PlayC");
        player3.setLocation(new Location(testWorld, 20, 4, 0));  // 20 blocks away

        Set<Player> recipients = new HashSet<>();
        recipients.add(player1);
        recipients.add(player2);
        recipients.add(player3);

        // test with large dropoff of 50 blocks - should keep all
        Ollivanders2Common.chatDropoff(recipients, 50, origin);

        assertTrue(recipients.contains(player1), "Player should be within 50 block dropoff");
        assertTrue(recipients.contains(player2), "Player should be within 50 block dropoff");
        assertTrue(recipients.contains(player3), "Player should be within 50 block dropoff");
        assertEquals(3, recipients.size(), "All 3 players should remain with 50 block dropoff");
    }

    /**
     * Tests chat dropoff with a very small radius.
     * Verifies that only players very close to the origin are kept with a small radius.
     */
    @Test
    void chatDropoffSmallRadiusTest() {
        // create players at close distances
        PlayerMock player1 = mockServer.addPlayer("X");
        player1.setLocation(new Location(testWorld, 0, 4, 0));  // 0 blocks away (at origin)

        PlayerMock player2 = mockServer.addPlayer("Y");
        player2.setLocation(new Location(testWorld, 1, 4, 0));  // 1 block away

        PlayerMock player3 = mockServer.addPlayer("Z");
        player3.setLocation(new Location(testWorld, 3, 4, 0));  // 3 blocks away

        Set<Player> recipients = new HashSet<>();
        recipients.add(player1);
        recipients.add(player2);
        recipients.add(player3);

        // test with small dropoff of 1 block - should only keep origin player
        Ollivanders2Common.chatDropoff(recipients, 1, origin);

        assertTrue(recipients.contains(player1), "Player at origin should be within 1 block dropoff");
        assertFalse(recipients.contains(player2), "Player at 1 block should be removed from 1 block dropoff (< not <=)");
        assertFalse(recipients.contains(player3), "Player at 3 blocks should be removed from 1 block dropoff");
        assertEquals(1, recipients.size(), "Only player at origin should remain with 1 block dropoff");
    }

    /**
     * Tests chat dropoff with an empty recipients set.
     * Verifies that the method handles empty sets gracefully without errors.
     */
    @Test
    void chatDropoffEmptyRecipientsTest() {
        // test with empty recipients set - should not throw
        Set<Player> recipients = new HashSet<>();

        Ollivanders2Common.chatDropoff(recipients, 10, origin);

        assertEquals(0, recipients.size(), "Empty set should remain empty after chatDropoff");
    }

    /**
     * Tests identifying door blocks.
     * Verifies that standard door blocks are correctly identified.
     */
    @Test
    void isDoorTest() {
        Block doorBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        doorBlock.setType(Material.DARK_OAK_DOOR);

        assertTrue(Ollivanders2Common.isDoor(doorBlock), "Ollivanders2Common.isDoor(doorBlock) returned false when block is Material.DARK_OAK_DOOR");
    }

    /**
     * Tests identifying trapdoors as doors.
     * Verifies that trapdoors are also recognized as door blocks.
     */
    @Test
    void isDoorTrapdoorTest() {
        Block trapdoorBlock = testWorld.getBlockAt(new Location(testWorld, 201, 4, 300));
        trapdoorBlock.setType(Material.DARK_OAK_TRAPDOOR);

        assertTrue(Ollivanders2Common.isDoor(trapdoorBlock), "Ollivanders2Common.isDoor(doorBlock) returned false when block is Material.DARK_OAK_TRAPDOOR");
    }

    /**
     * Tests that non-door blocks are not identified as doors.
     * Verifies that fences and other blocks are correctly distinguished from doors.
     */
    @Test
    void isDoorNotDoorTest() {
        Block notDoorBlock = testWorld.getBlockAt(new Location(testWorld, 202, 4, 300));
        notDoorBlock.setType(Material.DARK_OAK_FENCE);

        assertFalse(Ollivanders2Common.isDoor(notDoorBlock), "Ollivanders2Common.isDoor(notDoorBlock) returned true when block is Material.DARK_OAK_FENCE");
    }

    /**
     * Tests identifying chest blocks.
     * Verifies that standard chest blocks are correctly identified.
     */
    @Test
    void isChestTest() {
        Block chestBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 301));
        chestBlock.setType(Material.CHEST);

        assertTrue(Ollivanders2Common.isChest(chestBlock), "Ollivanders2Common.isChest(chestBlock) returned false when block is Material.CHEST");
    }

    /**
     * Tests identifying shulker boxes as container blocks.
     * Verifies that shulker boxes are recognized as valid storage containers.
     */
    @Test
    void isChestShulkerBoxTest() {
        Block shulkerBoxBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 302));
        shulkerBoxBlock.setType(Material.BLUE_SHULKER_BOX);

        assertTrue(Ollivanders2Common.isChest(shulkerBoxBlock), "Ollivanders2Common.isChest(chestBlock) returned false when block is Material.CHEST");
    }

    /**
     * Tests that non-container blocks are not identified as chests.
     * Verifies that barrels and other blocks are correctly distinguished from chests.
     */
    @Test
    void isChestNotChestTest() {
        Block notChestBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 303));
        notChestBlock.setType(Material.BARREL);

        assertFalse(Ollivanders2Common.isChest(notChestBlock), "Ollivanders2Common.isChest(notChestBlock) returned true when block is Material.BARREL");
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
        assertTrue(Ollivanders2Common.isAdjacentTo(centerBlock, aboveBlock), "Block above should be adjacent");

        // test adjacent BELOW (DOWN)
        Block belowBlock = testWorld.getBlockAt(new Location(testWorld, 100, 4, 100));
        belowBlock.setType(Material.DIRT);
        assertTrue(Ollivanders2Common.isAdjacentTo(centerBlock, belowBlock), "Block below should be adjacent");

        // test adjacent to the NORTH
        Block northBlock = testWorld.getBlockAt(new Location(testWorld, 100, 5, 99));
        northBlock.setType(Material.DIRT);
        assertTrue(Ollivanders2Common.isAdjacentTo(centerBlock, northBlock), "Block to the north should be adjacent");

        // test adjacent to the SOUTH
        Block southBlock = testWorld.getBlockAt(new Location(testWorld, 100, 5, 101));
        southBlock.setType(Material.DIRT);
        assertTrue(Ollivanders2Common.isAdjacentTo(centerBlock, southBlock), "Block to the south should be adjacent");

        // test adjacent to the EAST
        Block eastBlock = testWorld.getBlockAt(new Location(testWorld, 101, 5, 100));
        eastBlock.setType(Material.DIRT);
        assertTrue(Ollivanders2Common.isAdjacentTo(centerBlock, eastBlock), "Block to the east should be adjacent");

        // test adjacent to the WEST
        Block westBlock = testWorld.getBlockAt(new Location(testWorld, 99, 5, 100));
        westBlock.setType(Material.DIRT);
        assertTrue(Ollivanders2Common.isAdjacentTo(centerBlock, westBlock), "Block to the west should be adjacent");
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
        assertFalse(Ollivanders2Common.isAdjacentTo(centerBlock, diagonalBlock), "Diagonal block should not be adjacent");

        // test block 2 units away horizontally (not adjacent)
        Block twoAwayBlock = testWorld.getBlockAt(new Location(testWorld, 200, 5, 102));
        twoAwayBlock.setType(Material.DIRT);
        assertFalse(Ollivanders2Common.isAdjacentTo(centerBlock, twoAwayBlock), "Block 2 units away should not be adjacent");

        // test block 2 units away vertically (not adjacent)
        Block twoUpBlock = testWorld.getBlockAt(new Location(testWorld, 200, 7, 100));
        twoUpBlock.setType(Material.DIRT);
        assertFalse(Ollivanders2Common.isAdjacentTo(centerBlock, twoUpBlock), "Block 2 units above should not be adjacent");
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

        assertFalse(Ollivanders2Common.isAdjacentTo(block, block), "A block should not be adjacent to itself");
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
