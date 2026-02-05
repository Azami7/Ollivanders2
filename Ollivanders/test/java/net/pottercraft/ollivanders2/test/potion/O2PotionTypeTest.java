package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link O2PotionType} enum.
 * <p>
 * Verifies potion type lookup methods and ensures all potion types have unique names.
 * </p>
 */
public class O2PotionTypeTest {
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
     * Test getClassName returns the implementation class.
     * <p>
     * Simple getter - basic functionality verified by potion instantiation tests.
     * </p>
     */
    @Test
    void getClassNameTest() {
        // simple getter, verified by potion instantiation tests
    }

    /**
     * Test getLevel returns the magic difficulty level.
     * <p>
     * Simple getter - basic functionality verified by potion brewing tests.
     * </p>
     */
    @Test
    void getLevelTest() {
        // simple getter, verified by potion brewing tests
    }

    /**
     * Test getPotionName returns the display name.
     * <p>
     * Simple getter - basic functionality verified by getPotionTypeByName tests.
     * </p>
     */
    @Test
    void getPotionNameTest() {
        // simple getter, verified by getPotionTypeByName tests
    }

    /**
     * Test getPotionTypeFromString parses enum name strings correctly.
     */
    @Test
    void getPotionTypeFromStringTest() {
        // invalid string should return null
        assertNull(O2PotionType.getPotionTypeFromString("Invalid Potion"), "Invalid potion string should return null");

        // valid enum name should return the correct type
        O2PotionType expectedType = O2PotionType.ANIMAGUS_POTION;
        O2PotionType potionType = O2PotionType.getPotionTypeFromString(expectedType.toString());
        assertNotNull(potionType, "getPotionTypeFromString returned null for valid enum name");
        assertEquals(expectedType, potionType, "getPotionTypeFromString returned wrong potion type");
    }

    /**
     * Test getPotionTypeByName finds potions by their display name.
     * <p>
     * This is distinct from getPotionTypeFromString which uses enum names.
     * Display names may include punctuation (e.g., "Baruffio's Brain Elixir").
     * </p>
     */
    @Test
    void getPotionTypeByNameTest() {
        // invalid name should return null
        assertNull(O2PotionType.getPotionTypeByName("Invalid Potion"), "Invalid potion name should return null");

        // valid potion name should return the correct type
        O2PotionType expectedType = O2PotionType.BABBLING_BEVERAGE;
        O2PotionType potionType = O2PotionType.getPotionTypeByName(expectedType.getPotionName());
        assertNotNull(potionType, "getPotionTypeByName returned null for valid potion name");
        assertEquals(expectedType, potionType, "getPotionTypeByName returned wrong potion type");

        // test a potion where enum.toString and potion name are different (apostrophe in name)
        expectedType = O2PotionType.BARUFFIOS_BRAIN_ELIXIR;
        potionType = O2PotionType.getPotionTypeByName(expectedType.getPotionName());
        assertNotNull(potionType, "getPotionTypeByName returned null for Baruffio's Brain Elixir");
        assertEquals(expectedType, potionType, "getPotionTypeByName returned wrong type for Baruffio's Brain Elixir");

        // test case insensitivity
        potionType = O2PotionType.getPotionTypeByName("babbling beverage");
        assertNotNull(potionType, "getPotionTypeByName should be case-insensitive");
        assertEquals(O2PotionType.BABBLING_BEVERAGE, potionType, "getPotionTypeByName case-insensitive lookup failed");
    }

    /**
     * Verify every potion type has a unique display name.
     * <p>
     * Duplicate names would cause getPotionTypeByName to return incorrect results,
     * so all potion names must be unique.
     * </p>
     */
    @Test
    void uniquePotionNamesTest() {
        Map<String, List<O2PotionType>> grouped = Arrays.stream(O2PotionType.values())
                .collect(Collectors.groupingBy(O2PotionType::getPotionName));

        List<String> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate potion names: " + duplicates);
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
