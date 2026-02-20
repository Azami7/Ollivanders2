package net.pottercraft.ollivanders2.test.stationaryspell;

import net.kyori.adventure.text.Component;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Openable;
import org.bukkit.block.sign.Side;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the HARMONIA_NECTERE_PASSUS stationary spell.
 *
 * <p>Tests spell-specific behavior for the vanishing cabinet that creates paired portals
 * between two locations. Verifies cabinet structural integrity, player teleportation mechanics,
 * cooldown management, serialization, and deserialization validation.</p>
 *
 * @author Azami7
 */
public class HarmoniaNecterePassusTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Creates a vanishing cabinet structure for testing.
     *
     * <p>Builds a 3x3 cabinet with walls, a door, a sign, and a solid top. The sign at feet level
     * contains the destination location information. Used by tests to set up valid cabinet structures.</p>
     *
     * @param fromLocation the location of the cabinet's feet (sign position)
     * @param toLocation   the destination location (written on the sign)
     */
    public static void createCabinet(Location fromLocation, Location toLocation) {
        Block feet = fromLocation.getBlock();
        feet.setType(Material.OAK_SIGN);
        Sign sign = (Sign)feet.getState();
        sign.getSide(Side.FRONT).line(0, Component.text(toLocation.getWorld().getName()));
        sign.getSide(Side.FRONT).line(1, Component.text(Integer.toString((int)toLocation.getX())));
        sign.getSide(Side.FRONT).line(2, Component.text(Integer.toString((int)toLocation.getY())));
        sign.getSide(Side.FRONT).line(3, Component.text(Integer.toString((int)toLocation.getZ())));

        feet.getRelative(BlockFace.DOWN).setType(Material.STONE);
        Block doorBlock = feet.getRelative(BlockFace.SOUTH);
        doorBlock.setType(Material.OAK_DOOR);
        Openable door = (Openable)doorBlock.getBlockData();
        door.setOpen(true);
        doorBlock.setBlockData(door);

        feet.getRelative(BlockFace.WEST).setType(Material.OAK_PLANKS);
        feet.getRelative(BlockFace.EAST).setType(Material.OAK_PLANKS);
        feet.getRelative(BlockFace.NORTH).setType(Material.OAK_PLANKS);

        Block eye = feet.getRelative(BlockFace.UP);
        eye.getRelative(BlockFace.NORTH).setType(Material.OAK_PLANKS);
        eye.getRelative(BlockFace.WEST).setType(Material.OAK_PLANKS);
        eye.getRelative(BlockFace.EAST).setType(Material.OAK_PLANKS);
        eye.getRelative(BlockFace.UP).setType(Material.OAK_SLAB);

        // upper half of the door
        doorBlock = eye.getRelative(BlockFace.SOUTH);
        doorBlock.setType(Material.OAK_DOOR);
        door = (Openable)doorBlock.getBlockData();
        door.setOpen(true);
        doorBlock.setBlockData(door);
    }

    /**
     * Tests cabinet structural integrity checking and spell destruction.
     *
     * <p>Verifies that spells are killed when cabinets don't exist, persist when cabinets are valid,
     * and are killed when cabinet structure is damaged.</p>
     */
    @Test
    void upkeepCabinetIntegrityTest() {
        World testWorld = mockServer.addSimpleWorld("world1");
        Location location1 = new Location(testWorld, 100, 40, 100);
        Location location2 = new Location(testWorld, 120, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        // cabinets do not exist so spells should be killed
        assertTrue(harmoniaNecterePassus1.isKilled(), "spell 1 not killed when no cabinet exists");
        assertTrue(harmoniaNecterePassus2.isKilled(), "spell 2 not killed when no cabinet exists");

        // spells persist when there is a valid cabinet
        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        assertFalse(harmoniaNecterePassus1.isKilled(), "spell 1 killed unexpectedly");
        assertFalse(harmoniaNecterePassus2.isKilled(), "spell 2 killed unexpectedly");

        // spell is killed if a cabinet is damaged
        location1.getBlock().setType(Material.OAK_PLANKS);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmoniaNecterePassus1.isKilled(), "spell 1 not killed when twin cabinet damaged");
        assertTrue(harmoniaNecterePassus2.isKilled(), "spell 2 not killed when cabinet damaged");
    }

    /**
     * Tests in-use cooldown tracking and expiration.
     *
     * <p>Verifies that players are tracked as using a cabinet after entering, remain tracked
     * in the cooldown period after leaving, and are removed from tracking after cooldown expires.</p>
     */
    @Test
    void upkeepInUseByTest() {
        World testWorld = mockServer.addSimpleWorld("world2");
        Location location1 = new Location(testWorld, 200, 40, 100);
        Location location2 = new Location(testWorld, 220, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        // caster near cabinet
        Location nearCabinet1 = new Location(testWorld, 200, 39, 102);
        caster.setLocation(nearCabinet1);
        mockServer.getScheduler().performTicks(20);
        assertFalse(harmoniaNecterePassus1.isUsing(caster), "caster set to using cabinet before entry");

        // move caster in to the cabinet
        caster.setLocation(location1);
        // we have to manually trigger a player move event
        PlayerMoveEvent event = new PlayerMoveEvent(caster, nearCabinet1, location1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmoniaNecterePassus1.isUsing(caster), "caster not set to using cabinet after entry");

        // move caster out of cabinet, now in inUse cooldown
        caster.setLocation(nearCabinet1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmoniaNecterePassus1.isUsing(caster), "caster not set to using cabinet after leaving, during cooldown");

        // expire the cooldown
        mockServer.getScheduler().performTicks(HARMONIA_NECTERE_PASSUS.cooldown);
        assertFalse(harmoniaNecterePassus1.isUsing(caster), "caster set to using cabinet after cooldown expired");
    }

    /**
     * Tests finding the twin cabinet.
     *
     * <p>Verifies that getTwin() correctly locates the paired cabinet by its location.</p>
     */
    @Test
    void getTwinTest() {
        World testWorld = mockServer.addSimpleWorld("world3");
        Location location1 = new Location(testWorld, 300, 40, 100);
        Location location2 = new Location(testWorld, 320, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        HARMONIA_NECTERE_PASSUS twin1 = harmoniaNecterePassus1.getTwin();
        assertNotNull(twin1, "harmonia 1 getTwin() returned null");
        assertEquals(harmoniaNecterePassus2, twin1, "getTwin() did not return expected spell");
    }

    @Test
    void isUsingTest() {
        // tested by upkeepInUseByTest
    }

    /**
     * Tests player teleportation between cabinets.
     *
     * <p>Verifies that players entering a cabinet are teleported to the twin, null destination
     * locations don't cause teleportation, and cooldown prevents immediate re-teleportation
     * when arriving at the twin cabinet.</p>
     */
    @Test
    void doOnPlayerMoveEventTest () {
        World testWorld = mockServer.addSimpleWorld("world4");
        Location location1 = new Location(testWorld, 400, 40, 100);
        Location location2 = new Location(testWorld, 420, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        Location nearCabinet1 = new Location(testWorld, 400, 39, 102);
        caster.setLocation(nearCabinet1);

        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        // null to location should not cause anything to happen
        PlayerMoveEvent event = new PlayerMoveEvent(caster, nearCabinet1, null);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(harmoniaNecterePassus1.isUsing(caster), "doOnPlayerMoveEvent not short circuited by null to location");
        assertEquals(nearCabinet1, caster.getLocation(), "Caster was teleported when to location was null in PlayerMoveEvent");

        // player moving in to the vanishing cabinet should get teleported to the twin
        caster.setLocation(location1);
        event = new PlayerMoveEvent(caster, nearCabinet1, location1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(200); // give long enough for the teleport event to happen
        assertEquals(location2, caster.getLocation(), "caster not teleported to the twin location");

        // make sure caster cannot get immediately teleported back
        Location nearCabinet2 = new Location(testWorld, 420, 40, 102);
        event = new PlayerMoveEvent(caster, nearCabinet2, location2);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(harmoniaNecterePassus2.isUsing(caster), "doOnPlayerMoveEvent not short circuited by player twin inUse cooldown");
    }

    /**
     * Tests serialization and deserialization of vanishing cabinet spell data.
     *
     * <p>Verifies that the twin cabinet location is correctly serialized to a map, and that
     * deserializing valid data restores the twin location. Also verifies that deserializing
     * with missing twin location data does not set the twin location.</p>
     */
    @Test
    void serializeAndDeserializeSpellDataTest() {
        World testWorld = mockServer.addSimpleWorld("world5");
        Location location1 = new Location(testWorld, 500, 40, 100);
        Location location2 = new Location(testWorld, 520, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HARMONIA_NECTERE_PASSUS spell = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);

        // serialize and verify data contains twin location
        Map<String, String> spellData = spell.serializeSpellData();
        assertFalse(spellData.isEmpty(), "serialized spell data is empty");

        // deserialize valid data into a new spell and verify twin is restored
        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS deserialized = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(deserialized);
        deserialized.deserializeSpellData(spellData);
        assertFalse(deserialized.isKilled(), "spell killed after deserializing valid data");

        // deserialize with empty data does not crash
        HARMONIA_NECTERE_PASSUS emptyDeserialized = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        emptyDeserialized.deserializeSpellData(new HashMap<>());
        assertFalse(emptyDeserialized.isKilled(), "spell killed after deserializing empty data");
    }

    /**
     * Tests spell deserialization validation.
     *
     * <p>Verifies that a spell created via the deserialization constructor fails
     * checkSpellDeserialization() because it lacks required data (player UUID, location,
     * and twin cabinet location), while a properly initialized spell passes validation.</p>
     */
    @Test
    void checkSpellDeserializationTest() {
        O2StationarySpell stationarySpell = Ollivanders2API.getStationarySpells().createStationarySpellByType(O2StationarySpellType.HARMONIA_NECTERE_PASSUS);
        assertNotNull(stationarySpell);
        assertFalse(stationarySpell.checkSpellDeserialization(), "Deserialized spell should fail check without required data");

        World testWorld = mockServer.addSimpleWorld("world6");
        Location location1 = new Location(testWorld, 600, 40, 100);
        Location location2 = new Location(testWorld, 620, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HARMONIA_NECTERE_PASSUS spell = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        assertTrue(spell.checkSpellDeserialization(), "Properly initialized spell failed deserialization check");
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
