package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.HERBICIDE;
import net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS;
import net.pottercraft.ollivanders2.stationaryspell.MOLLIARE;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link net.pottercraft.ollivanders2.stationaryspell.O2StationarySpells} manager.
 *
 * <p>Tests the core functionality of the stationary spells manager including spell addition,
 * removal, querying by location and type, and creation of spell instances via reflection.
 * Event handler behavior is tested by individual spell implementation test classes.</p>
 *
 * <p>Note: Serialization/deserialization tests are deferred to integration tests to avoid
 * file I/O dependencies that could cause flakiness in parallel test execution.</p>
 */
public class O2StationarySpellsTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

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
    }

    /**
     * Loads a fresh plugin instance with default configuration.
     *
     * <p>Each test method calls this to get a properly initialized plugin with all managers
     * and event listeners registered. The server scheduler is advanced to complete plugin startup.</p>
     *
     * @return a fresh Ollivanders2 plugin instance
     */
    Ollivanders2 getMockPlugin() {
        Ollivanders2 testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
        return testPlugin;
    }

    /**
     * Tests adding and removing stationary spells.
     *
     * <p>Verifies that a spell can be added to the manager, becomes available at its location,
     * and is properly removed when marked as killed.</p>
     */
    @Test
    void addAndRemoveStationarySpellTest() {
        Ollivanders2 testPlugin = getMockPlugin();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();

        LUMOS_FERVENS lumosFervens = new LUMOS_FERVENS(testPlugin, player.getUniqueId(), location, 5, 1000);

        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        assertTrue(lumosFervens.isActive(), "");
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, lumosFervens.getSpellType()), "");

        Ollivanders2API.getStationarySpells().removeStationarySpell(lumosFervens);
        assertTrue(lumosFervens.isKilled(), "");
        mockServer.getScheduler().performTicks(20);
        assertFalse(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, lumosFervens.getSpellType()), "");
    }

    /**
     * Tests retrieving active stationary spells.
     *
     * <p>Verifies that the manager correctly identifies active spells and excludes killed ones.</p>
     */
    @Test
    void getActiveStationarySpellsTest() {
        Ollivanders2 testPlugin = getMockPlugin();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();

        List<O2StationarySpell> activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpells();
        assertTrue(activeSpells.isEmpty(), "");

        ALIQUAM_FLOO aliquamFloo = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location, "floo1");
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo);

        activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpells();
        assertEquals(1, activeSpells.size(), "");

        aliquamFloo.kill();
        activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpells();
        assertTrue(activeSpells.isEmpty(), "");
    }

    /**
     * Tests finding spells at a location.
     *
     * <p>Verifies that spells are correctly found at their location and removed from results when killed.</p>
     */
    @Test
    void getStationarySpellsAtLocationTest() {
        Ollivanders2 testPlugin = getMockPlugin();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();

        List<O2StationarySpell> activeSpells = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);
        assertTrue(activeSpells.isEmpty(), "");

        CAVE_INIMICUM caveInimicum = new CAVE_INIMICUM(testPlugin, player.getUniqueId(), location, 5, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(caveInimicum);
        activeSpells = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);
        assertEquals(1, activeSpells.size(), "");

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        activeSpells = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);
        assertEquals(2, activeSpells.size(), "");

        caveInimicum.kill();
        mockServer.getScheduler().performTicks(20);
        activeSpells = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);
        assertEquals(1, activeSpells.size(), "");
    }

    /**
     * Tests finding active spells of a specific type at a location.
     *
     * <p>Verifies that spells are filtered by both location and type, and that killed spells are excluded.</p>
     */
    @Test
    void getActiveStationarySpellsAtLocationByTypeTest() {
        Ollivanders2 testPlugin = getMockPlugin();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();

        List<O2StationarySpell> activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.CAVE_INIMICUM);
        assertTrue(activeSpells.isEmpty(), "");

        CAVE_INIMICUM caveInimicum = new CAVE_INIMICUM(testPlugin, player.getUniqueId(), location, 5, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(caveInimicum);
        activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.CAVE_INIMICUM);
        assertEquals(1, activeSpells.size(), "");

        caveInimicum = new CAVE_INIMICUM(testPlugin, player.getUniqueId(), location, 5, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(caveInimicum);
        activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.CAVE_INIMICUM);
        assertEquals(2, activeSpells.size(), "");

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, player.getUniqueId(), location);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.CAVE_INIMICUM);
        assertEquals(2, activeSpells.size(), "");

        caveInimicum.kill();
        mockServer.getScheduler().performTicks(20);
        activeSpells = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.CAVE_INIMICUM);
        assertEquals(1, activeSpells.size(), "");
    }

    /**
     * Tests checking for a spell of a specific type at a location.
     *
     * <p>Verifies that the manager correctly identifies whether a location has an active spell
     * of a given type, and returns false when no spell of that type is present.</p>
     */
    @Test
    void checkLocationForStationarySpellTest() {
        Ollivanders2 testPlugin = getMockPlugin();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();

        assertFalse(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "");

        HERBICIDE herbicide = new HERBICIDE(testPlugin, player.getUniqueId(), location, 5, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(herbicide);
        assertFalse(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "");

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus = new HARMONIA_NECTERE_PASSUS(testPlugin, player.getUniqueId(), location, new Location(testWorld, 200, 4, 100));
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus);
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "");
    }

    /**
     * Tests determining whether a location is inside a spell's radius.
     *
     * <p>Verifies that locations within a spell's radius return true and locations outside
     * return false, using a radius-based distance check.</p>
     */
    @Test
    void isInsideOfTest() {
        Ollivanders2 testPlugin = getMockPlugin();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();

        MOLLIARE molliare = new MOLLIARE(testPlugin, player.getUniqueId(), location, 5, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(molliare);

        assertFalse(Ollivanders2API.getStationarySpells().isInsideOf(O2StationarySpellType.HERBICIDE, location), "");
        assertTrue(Ollivanders2API.getStationarySpells().isInsideOf(O2StationarySpellType.MOLLIARE, location), "");
        assertTrue(Ollivanders2API.getStationarySpells().isInsideOf(O2StationarySpellType.MOLLIARE, new Location(location.getWorld(), location.getX() + 1, location.getY(), location.getZ())), "");
        assertFalse(Ollivanders2API.getStationarySpells().isInsideOf(O2StationarySpellType.MOLLIARE, new Location(location.getWorld(), location.getX() + 10, location.getY(), location.getZ())), "");
    }

    /**
     * Tests the upkeep mechanism for active spells.
     *
     * <p>Note: Individual spell upkeep behavior is tested by each spell's specific test class.
     * This test is included for completeness in test coverage.</p>
     */
    @Test
    void upkeepTest() {
        // tested by each spells upkeep tests
    }

    /**
     * Tests persisting active spells to storage.
     *
     * <p>Note: Serialization/deserialization is deferred to integration tests to avoid file I/O
     * dependencies and flakiness that could occur from parallel test execution modifying shared
     * save files non-deterministically.</p>
     */
    @Test
    void saveO2StationarySpellsTest() {
        // cannot test this in unit tests because they run in parallel and each is
        // altering the save files non-deterministically
    }

    /**
     * Tests restoring persisted spells from storage.
     *
     * <p>Note: Serialization/deserialization is deferred to integration tests to avoid file I/O
     * dependencies and flakiness that could occur from parallel test execution modifying shared
     * save files non-deterministically.</p>
     */
    @Test
    void loadO2StationarySpellsTest() {
        // cannot test this in unit tests because they run in parallel and each is
        // altering the save files non-deterministically
    }

    /**
     * Tests creating spell instances via reflection by spell type.
     *
     * <p>Verifies that the reflection-based spell factory correctly instantiates all spell types,
     * that created spells have the correct spell type, and that spells start inactive until
     * their data is fully loaded. Tests both simple spells and spells with unique initialization
     * requirements.</p>
     */
    @Test
    void createStationarySpellByTypeTest() {
        getMockPlugin(); // initialize plugin

        // test creating a simple spell type
        O2StationarySpell herbicide = Ollivanders2API.getStationarySpells().createStationarySpellByType(O2StationarySpellType.HERBICIDE);
        assertNotNull(herbicide, "createStationarySpellByType should return a spell");
        assertEquals(O2StationarySpellType.HERBICIDE, herbicide.getSpellType(), "Created spell should have correct type");
        assertFalse(herbicide.isActive(), "Created spell should be inactive by default");

        // test creating a spell with unique data requirements
        O2StationarySpell aliquamFloo = Ollivanders2API.getStationarySpells().createStationarySpellByType(O2StationarySpellType.ALIQUAM_FLOO);
        assertNotNull(aliquamFloo, "createStationarySpellByType should return a spell for ALIQUAM_FLOO");
        assertEquals(O2StationarySpellType.ALIQUAM_FLOO, aliquamFloo.getSpellType(), "Created spell should have correct type");

        // test creating multiple different spell types
        for (O2StationarySpellType spellType : O2StationarySpellType.values()) {
            O2StationarySpell spell = Ollivanders2API.getStationarySpells().createStationarySpellByType(spellType);
            assertNotNull(spell, "createStationarySpellByType should return a spell for " + spellType);
            assertEquals(spellType, spell.getSpellType(), "Created spell should have correct type for " + spellType);
        }

        // clean up any floo spells that were added to the static list
        ALIQUAM_FLOO.clearFlooNetwork();
    }

    /**
     * Cleans up state after each test to ensure test isolation.
     *
     * <p>Kills all active spells and clears the static floo network list to prevent
     * test pollution from affecting subsequent tests.</p>
     */
    @AfterEach
    void cleanUp() {
        // kill all active stationary spells
        if (Ollivanders2API.getStationarySpells() != null) {
            for (O2StationarySpell spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
                spell.kill();
            }

        // run upkeep to remove killed spells from the list
        mockServer.getScheduler().performTicks(20);
    }

        // clear the static floo network list
        ALIQUAM_FLOO.clearFlooNetwork();
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
