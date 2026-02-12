package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link O2StationarySpellType}.
 *
 * <p>This test class verifies the behavior of the O2StationarySpellType enum, including spell
 * name retrieval, type lookup, player location detection within spell areas, and uniqueness
 * constraints. Tests use MockBukkit for server simulation and mock spell instances.</p>
 */
public class O2StationarySpellTypeTest {
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
     * Tests the {@link O2StationarySpellType#getClassName()} method.
     *
     * <p>This is a simple getter method; comprehensive behavior is tested indirectly through
     * other test methods.</p>
     */
    @Test
    void getClassNameTest() {
        // simple getter, skipping tests
    }

    /**
     * Tests the {@link O2StationarySpellType#getLevel()} method.
     *
     * <p>This is a simple getter method; comprehensive behavior is tested indirectly through
     * other test methods.</p>
     */
    @Test
    void getLevelTest() {
        // simple getter, skipping tests
    }

    /**
     * Tests the {@link O2StationarySpellType#getStationarySpellTypeFromString(String)} method.
     *
     * <p>Verifies that the method returns null for invalid spell names and correctly retrieves
     * the appropriate spell type for valid spell names.</p>
     */
    @Test
    void getStationarySpellTypeFromStringTest() {
        O2StationarySpellType expectedType = O2StationarySpellType.HERBICIDE;

        assertNull(O2StationarySpellType.getStationarySpellTypeFromString("Invalid String"), "");

        O2StationarySpellType stationarySpellType = O2StationarySpellType.getStationarySpellTypeFromString(expectedType.getSpellName());
        assertNotNull(stationarySpellType, "O2StationarySpellType.getStationarySpellTypeFromString(" + expectedType.getSpellName() + ") returned null");
        assertEquals(expectedType, stationarySpellType, "");
    }

    /**
     * Tests the {@link O2StationarySpellType#getSpellName()} method.
     *
     * <p>Verifies that the spell name is correctly retrieved for a spell type. Tests the
     * HERBICIDE spell type to ensure it returns the expected name.</p>
     */
    @Test
    void getSpellNameTest() {
        assertEquals("Herbicide", O2StationarySpellType.HERBICIDE.getSpellName(), "");
    }

    /**
     * Tests the {@link O2StationarySpellType#isPlayerInsideStationarySpell(org.bukkit.entity.Player)} method.
     *
     * <p>Verifies that the method correctly detects whether a player is within the radius of a
     * stationary spell. Tests a MUFFLIATO spell with a 5-block radius, confirming detection when
     * the player is inside the spell area and no detection when outside.</p>
     */
    @Test
    void isPlayerInsideStationarySpellTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(location);
        MUFFLIATO muffliato = new MUFFLIATO(testPlugin, player.getUniqueId(), location, 5, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(muffliato);
        mockServer.getScheduler().performTicks(20);

        assertTrue(O2StationarySpellType.MUFFLIATO.isPlayerInsideStationarySpell(player), "");
        player.setLocation(new Location(testWorld, 200, 4, 100));
        assertFalse(O2StationarySpellType.MUFFLIATO.isPlayerInsideStationarySpell(player), "");
    }

    /**
     * Tests that all stationary spells have unique names.
     *
     * <p>Verifies that every O2StationarySpellType enum constant has a distinct spell name.
     * This constraint ensures that spells can be uniquely identified by name throughout the
     * application.</p>
     */
    @Test
    void uniquePotionNamesTest() {
        Map<String, List<O2StationarySpellType>> grouped = Arrays.stream(O2StationarySpellType.values())
                .collect(Collectors.groupingBy(O2StationarySpellType::getSpellName));

        List<String> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate stationary spell names: " + duplicates);
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
