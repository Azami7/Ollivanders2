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
 * Unit tests for the {@link net.pottercraft.ollivanders2.stationaryspell.O2StationarySpells} manager: adding, removing,
 * and querying spells by location and type, and creating spell instances by type. Per-spell event handling is covered
 * by each spell's own test class.
 */
public class O2StationarySpellsTest {
    static ServerMock mockServer;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;
        mockServer = MockBukkit.mock();
    }

    /**
     * Load a fresh plugin instance with all managers and listeners registered, advancing the scheduler through startup.
     *
     * @return a fresh Ollivanders2 plugin instance
     */
    Ollivanders2 getMockPlugin() {
        Ollivanders2 testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
        return testPlugin;
    }

    /**
     * An added spell is present at its location and is gone after being removed and the next upkeep.
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
     * The active-spells list includes an added spell and excludes it once killed.
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
     * The location query returns every spell whose radius covers the location and drops killed ones.
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
     * The by-type location query returns only active spells matching both the location and the type.
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
     * The location-and-type check is true only when a spell of that type covers the location.
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
     * isInsideOf is true for locations within an active spell's radius and false beyond it or for an absent type.
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

    @Test
    void upkeepTest() {
        // tested by each spell's own upkeep test
    }

    @Test
    void saveO2StationarySpellsTest() {
        // cannot test this in unit tests because they run in parallel and each is
        // altering the save files non-deterministically
    }

    @Test
    void loadO2StationarySpellsTest() {
        // cannot test this in unit tests because they run in parallel and each is
        // altering the save files non-deterministically
    }

    /**
     * The factory creates an inactive instance of the correct type for every spell type.
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
     * Kill all active spells and clear the static floo network so tests do not pollute each other.
     */
    @AfterEach
    void cleanUp() {
        if (Ollivanders2API.getStationarySpells() != null) {
            for (O2StationarySpell spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
                spell.kill();
            }

            // run upkeep to remove the killed spells from the list
            mockServer.getScheduler().performTicks(20);
        }

        ALIQUAM_FLOO.clearFlooNetwork();
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
