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
 * Unit tests for {@link Ollivanders2Common}.
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
     * A correctly formatted UUID string parses to a non-null UUID.
     */
    @Test
    void uuidFromStringTest() {
        String uuidString = "5d2bec8f-2402-4039-920b-bdbf348fa9a7";
        UUID uuid = o2common.uuidFromString(uuidString);
        assertNotNull(uuid, "o2common.uuidFromString() returned null for " + uuidString);
    }

    /**
     * An unparseable UUID string returns null rather than throwing.
     */
    @Test
    void uuidFromStringBadStringTest() {
        String uuidBadString = "badstring";
        UUID uuid = o2common.uuidFromString(uuidBadString);
        assertNull(uuid, "o2common.uuidFromString(uuidBadString) did not return null");
    }

    /**
     * A numeric string parses to a non-null Integer.
     */
    @Test
    void integerFromStringTest() {
        String integerString = "12345";
        Integer integer = o2common.integerFromString(integerString);
        assertNotNull(integer, "o2common.integerFromString() returned null for " + integerString);
    }

    /**
     * A non-numeric string returns null rather than throwing.
     */
    @Test
    void integerFromStringBadStringTest() {
        String integerBadString = "badstring";
        Integer integer = o2common.integerFromString(integerBadString);
        assertNull(integer, "o2common.integerFromString(uuidBadString) did not return null");
    }

    /**
     * A "true" string parses to a non-null Boolean.
     */
    @Test
    void booleanFromStringTest() {
        String boolString = "true";
        Boolean bool = o2common.booleanFromString(boolString);
        assertNotNull(bool, "o2common.booleanFromString() returned null for " + boolString);
    }

    /**
     * A non-boolean string returns false rather than throwing.
     */
    @Test
    void booleanFromStringBadStringTest() {
        String boolBadString = "badstring";
        Boolean bool = o2common.booleanFromString(boolBadString);
        assertFalse(bool, "o2common.booleanFromString(boolBadString) did not return false");
    }

    /**
     * A location within the radius is reported as inside.
     */
    @Test
    void isInsideTest() {
        Location checkLocation = new Location(testWorld, 5, 4, 0);

        assertTrue(Ollivanders2Common.isInside(origin, checkLocation, 10));
    }

    /**
     * A location beyond the radius is reported as outside.
     */
    @Test
    void isInsideOutsideTest() {
        Location checkLocation = new Location(testWorld, 15, 4, 0);

        assertFalse(Ollivanders2Common.isInside(origin, checkLocation, 10));
    }

    /**
     * An enum name is recoded to lowercase space-separated words, e.g. "FUMOS_DUO" -&gt; "fumos duo".
     */
    @Test
    void enumRecodeTest() {
        String sourceString = "FUMOS_DUO";
        String expectedString = "fumos duo";
        String enumString = Ollivanders2Common.enumRecode(sourceString);

        assertEquals(expectedString, enumString, "Ollivanders2Common.enumRecode(sourceString) Expected: " + expectedString + " Actual: " + enumString);
    }

    /**
     * The first letter of each word is capitalized, e.g. "fumos duo" -&gt; "Fumos Duo".
     */
    @Test
    void firstLetterCapitalizeTest() {
         String sourceString = "fumos duo";
         String expectedString = "Fumos Duo";
         String capitalizedString = Ollivanders2Common.firstLetterCapitalize(sourceString);

         assertEquals(expectedString, capitalizedString, "Ollivanders2Common.firstLetterCapitalize(sourceString) Expected: " + expectedString + ", Actual: " + capitalizedString);
    }

    /**
     * A Location serializes to a map whose keys are prefixed with the given label.
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
     * A location with a null world is rejected and serializes to null.
     */
    @Test
    void serializeLocationBadLocationTest() {
        Location badLocation = new Location(null, 0, 4, 0);
        Map<String, String> actual = o2common.serializeLocation(badLocation, "test");
        assertNull(actual, "o2common.serializeLocation(badLocation, \"test\") did not return null");
    }

    /**
     * A serialized location round-trips back to a Location with the original world and coordinates.
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
     * Deserialization returns null when a coordinate is null or missing.
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
     * The faced block is returned when its type matches the requested type.
     */
    @Test
    void playerFacingBlockTypeTest() {
        Block block = testWorld.getBlockAt(origin);

        Block facingBlock = playerFacingBlockTypeHelper(Material.STONE, Material.STONE);
        assertNotNull(facingBlock, "Ollivanders2Common.playerFacingBlockType() should return a block when facing STONE");
        assertEquals(block, facingBlock, "Should return the block at origin");
    }

    /**
     * Null is returned when the faced block type does not match the requested type.
     */
    @Test
    void playerFacingBlockTypeFailureTest() {
        Block facingBlock = playerFacingBlockTypeHelper(Material.DARK_OAK_WOOD, Material.STONE);
        assertNull(facingBlock, "Ollivanders2Common.playerFacingBlockType() should return null facing WOOD");
    }

    /**
     * Null is returned when the player faces air and no block of the requested type is in line of sight.
     */
    @Test
    void playerFacingBlockTypeNothingTest() {
        Block facingBlock = playerFacingBlockTypeHelper(Material.AIR, Material.STONE);
        assertNull(facingBlock, "Ollivanders2Common.playerFacingBlockType() should return null when not facing the block type");
    }

    Block playerFacingBlockTypeHelper(@NotNull Material originBlockType, @NotNull Material checkBlockType) {
        Block block = testWorld.getBlockAt(origin);
        block.setType(originBlockType);

        PlayerMock player = mockServer.addPlayer();
        Location playerLocation = new Location(testWorld, 0, 4, 3);
        player.setLocation(playerLocation);

        // yaw=180 faces negative Z (toward origin), pitch=0 keeps the look horizontal
        player.setRotation(180, 0);

        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);

        return Ollivanders2Common.playerFacingBlockType(player, checkBlockType);
    }

    /**
     * A spherical coordinate converts to a vector whose magnitude equals the radius.
     */
    @Test
    void sphereToVectorTest() {
        double radius = 10;

        // Test 1: north pole (inclination = 0, azimuth = 0)
        Vector northPole = Ollivanders2Common.sphereToVector(new double[]{0, 0}, (int) radius);
        assertNotNull(northPole, "sphereToVector() should return a vector");
        assertEquals(radius, northPole.length(), 0.01, "Vector magnitude should equal radius");

        // Test 2: equator (inclination = π/2, azimuth = 0)
        Vector equator = Ollivanders2Common.sphereToVector(new double[]{Math.PI / 2, 0}, (int) radius);
        assertNotNull(equator, "sphereToVector() should return a vector");
        assertEquals(radius, equator.length(), 0.01, "Vector magnitude should equal radius");
    }

    /**
     * flair() runs without error for its overloads and a null location. MockBukkit exposes no particle system, so the
     * visual effect itself cannot be asserted.
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
     * Every player in the list receives the title and subtitle.
     */
    @Test
    void sendTitleMessageTest() {
        PlayerMock player1 = mockServer.addPlayer("Fred");
        PlayerMock player2 = mockServer.addPlayer("Joe");

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

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
     * Players within the radius receive the message; a player outside it does not.
     */
    @Test
    void sendMessageInRadius() {
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
        assertEquals(testMessage, TestCommon.cleanChatMessage(received), "Player1 did not receive correct message");

        received = player2.nextMessage();
        assertNotNull(received, "player2.nextMessage() was null");
        assertEquals(testMessage, TestCommon.cleanChatMessage(received), "Player2 did not receive correct message");

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
     * A debug message is logged when {@link Ollivanders2#debug} is true.
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
     * A debug message is suppressed when {@link Ollivanders2#debug} is false.
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
     * A debug message logs at WARNING level when the warning flag is set.
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
     * A log message is recorded regardless of debug mode.
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
     * A log message logs at WARNING level when the warning flag is set.
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
     * Identical locations, whether the same instance or equal coordinates, compare as equal.
     */
    @Test
    void locationEqualsTest() {
        assertTrue(Ollivanders2Common.locationEquals(origin, origin), "Ollivanders2Common.locationEquals(origin, origin) did not return true");

        Location loc1 = new Location(testWorld, 5, 5, 5);
        Location loc2 = new Location(testWorld, 5, 5, 5);
        assertTrue(Ollivanders2Common.locationEquals(loc1, loc2), "Ollivanders2Common.locationEquals(loc1, loc2) did not return true when locations identical");
    }

    /**
     * Locations differing in coordinates or world compare as unequal.
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
     * A dropoff removes recipients beyond the radius and keeps those within it.
     */
    @Test
    void chatDropoffTest() {
        PlayerMock player1 = mockServer.addPlayer("Close1");
        player1.setLocation(new Location(testWorld, 2, 4, 0));  // 2 blocks away

        PlayerMock player2 = mockServer.addPlayer("Close2");
        player2.setLocation(new Location(testWorld, 0, 4, 3));  // 3 blocks away

        PlayerMock player3 = mockServer.addPlayer("Far1");
        player3.setLocation(new Location(testWorld, 10, 4, 0));  // 10 blocks away

        PlayerMock player4 = mockServer.addPlayer("Far2");
        player4.setLocation(new Location(testWorld, 0, 4, 15));  // 15 blocks away

        Set<Player> recipients = new HashSet<>();
        recipients.add(player1);
        recipients.add(player2);
        recipients.add(player3);
        recipients.add(player4);

        Ollivanders2Common.chatDropoff(recipients, 5, origin);

        assertTrue(recipients.contains(player1), "Player at 2 blocks should be within 5 block dropoff");
        assertTrue(recipients.contains(player2), "Player at 3 blocks should be within 5 block dropoff");

        assertFalse(recipients.contains(player3), "Player at 10 blocks should be removed from 5 block dropoff");
        assertFalse(recipients.contains(player4), "Player at 15 blocks should be removed from 5 block dropoff");

        assertEquals(2, recipients.size(), "Expected 2 recipients remaining after 5 block dropoff");
    }

    /**
     * A radius larger than any player's distance keeps every recipient.
     */
    @Test
    void chatDropoffLargeRadiusTest() {
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

        Ollivanders2Common.chatDropoff(recipients, 50, origin);

        assertTrue(recipients.contains(player1), "Player should be within 50 block dropoff");
        assertTrue(recipients.contains(player2), "Player should be within 50 block dropoff");
        assertTrue(recipients.contains(player3), "Player should be within 50 block dropoff");
        assertEquals(3, recipients.size(), "All 3 players should remain with 50 block dropoff");
    }

    /**
     * A 1-block radius keeps only the player at the origin; the bound is exclusive, so a player exactly 1 block away is
     * dropped.
     */
    @Test
    void chatDropoffSmallRadiusTest() {
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

        Ollivanders2Common.chatDropoff(recipients, 1, origin);

        assertTrue(recipients.contains(player1), "Player at origin should be within 1 block dropoff");
        assertFalse(recipients.contains(player2), "Player at 1 block should be removed from 1 block dropoff (< not <=)");
        assertFalse(recipients.contains(player3), "Player at 3 blocks should be removed from 1 block dropoff");
        assertEquals(1, recipients.size(), "Only player at origin should remain with 1 block dropoff");
    }

    /**
     * An empty recipients set is handled without error and stays empty.
     */
    @Test
    void chatDropoffEmptyRecipientsTest() {
        Set<Player> recipients = new HashSet<>();

        Ollivanders2Common.chatDropoff(recipients, 10, origin);

        assertEquals(0, recipients.size(), "Empty set should remain empty after chatDropoff");
    }

    /**
     * A standard door block is identified as a door.
     */
    @Test
    void isDoorTest() {
        Block doorBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        doorBlock.setType(Material.DARK_OAK_DOOR);

        assertTrue(Ollivanders2Common.isDoor(doorBlock), "Ollivanders2Common.isDoor() returned false when block is Material.DARK_OAK_DOOR");
    }

    /**
     * A trapdoor is identified as a door.
     */
    @Test
    void isDoorTrapdoorTest() {
        Block trapdoorBlock = testWorld.getBlockAt(new Location(testWorld, 201, 4, 300));
        trapdoorBlock.setType(Material.DARK_OAK_TRAPDOOR);

        assertTrue(Ollivanders2Common.isDoor(trapdoorBlock), "Ollivanders2Common.isDoor() returned false when block is Material.DARK_OAK_TRAPDOOR");
    }

    /**
     * A fence gate is identified as a door.
     */
    @Test
    void isDoorGateTest() {
        Block gateBlock = testWorld.getBlockAt(new Location(testWorld, 201, 4, 500));
        gateBlock.setType(Material.DARK_OAK_FENCE_GATE);

        assertTrue(Ollivanders2Common.isDoor(gateBlock), "Ollivanders2Common.isDoor() returned false when block is Material.DARK_OAK_FENCE_GATE");
    }

    /**
     * A block that is not a door, trapdoor, or gate is not identified as a door.
     */
    @Test
    void isDoorNotDoorTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 202, 4, 600));
        block.setType(Material.STONE);

        assertFalse(Ollivanders2Common.isDoor(block), "Ollivanders2Common.isDoor() returned true when block is Material.STONE");
    }

    /**
     * A standard chest block is identified as a chest.
     */
    @Test
    void isChestTest() {
        Block chestBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 301));
        chestBlock.setType(Material.CHEST);

        assertTrue(Ollivanders2Common.isChest(chestBlock), "Ollivanders2Common.isChest() returned false when block is Material.CHEST");
    }

    /**
     * A shulker box is identified as a chest.
     */
    @Test
    void isChestShulkerBoxTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 302));
        block.setType(Material.BLUE_SHULKER_BOX);

        assertTrue(Ollivanders2Common.isChest(block), "Ollivanders2Common.isChest() returned false when block is Material.BLUE_SHULKER_BOX");
    }

    /**
     * A barrel is not identified as a chest.
     */
    @Test
    void isChestNotChestTest() {
        Block notChestBlock = testWorld.getBlockAt(new Location(testWorld, 200, 4, 303));
        notChestBlock.setType(Material.BARREL);

        assertFalse(Ollivanders2Common.isChest(notChestBlock), "Ollivanders2Common.isChest() returned true when block is Material.BARREL");
    }

    /**
     * A standing sign is identified as a sign.
     */
    @Test
    void isSignStandingSignTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 400));
        block.setType(Material.OAK_SIGN);

        assertTrue(Ollivanders2Common.isSign(block), "Ollivanders2Common.isSign() returned false when block is Material.OAK_SIGN");
    }

    /**
     * A wall sign is identified as a sign.
     */
    @Test
    void isSignWallSignTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 401));
        block.setType(Material.OAK_WALL_SIGN);

        assertTrue(Ollivanders2Common.isSign(block), "Ollivanders2Common.isSign() returned false when block is Material.OAK_WALL_SIGN");
    }

    /**
     * A hanging sign is identified as a sign.
     */
    @Test
    void isSignHangingSignTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 402));
        block.setType(Material.OAK_HANGING_SIGN);

        assertTrue(Ollivanders2Common.isSign(block), "Ollivanders2Common.isSign() returned false when block is Material.OAK_HANGING_SIGN");
    }

    /**
     * A barrel is not identified as a sign.
     */
    @Test
    void isSignNotSignTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 403));
        block.setType(Material.BARREL);

        assertFalse(Ollivanders2Common.isSign(block), "Ollivanders2Common.isSign() returned true when block is Material.BARREL");
    }

    /**
     * A log block is identified as a natural log.
     */
    @Test
    void isNaturalLogLogTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 404));
        block.setType(Material.OAK_LOG);

        assertTrue(Ollivanders2Common.isNaturalLog(block), "Ollivanders2Common.isNaturalLog() returned true when block is Material.OAK_LOG");
    }

    /**
     * A nether stem is identified as a natural log.
     */
    @Test
    void isNaturalLogStemTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 405));
        block.setType(Material.WARPED_STEM);

        assertTrue(Ollivanders2Common.isNaturalLog(block), "Ollivanders2Common.isNaturalLog() returned true when block is Material.WARPED_STEM");
    }

    /**
     * A barrel is not identified as a natural log.
     */
    @Test
    void isNaturalLogNotLogTest() {
        Block block = testWorld.getBlockAt(new Location(testWorld, 200, 4, 406));
        block.setType(Material.BARREL);

        assertFalse(Ollivanders2Common.isNaturalLog(block), "Ollivanders2Common.isNaturalLog() returned true when block is Material.BARREL");
    }

    /**
     * Every chest and shulker box material is recognized by isChest, guarding against a material missed during list
     * initialization.
     */
    @Test
    void chestMaterialsTest() {
        Ollivanders2Common.initMaterialLists();

        for (Material material : Material.values()) {
            // skip legacy materials — Ollivanders2 does not support LEGACY_* entries
            if (material.toString().startsWith("LEGACY_"))
                continue;
            if (material.toString().endsWith("_CHEST") || material.toString().endsWith("_SHULKER_BOX")) {
                assertTrue(Ollivanders2Common.isChest(material), "Missing chest material " + material);
            }
        }
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
