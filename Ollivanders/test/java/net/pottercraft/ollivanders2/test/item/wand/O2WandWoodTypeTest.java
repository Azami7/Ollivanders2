package net.pottercraft.ollivanders2.test.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link O2WandWoodType} enum functionality.
 * <p>
 * Verifies that wand wood type retrieval, filtering, random selection, and item validation
 * work correctly across all wood types. Tests ensure deterministic behavior for seed-based
 * selection and proper material-to-type mapping.
 * </p>
 */
public class O2WandWoodTypeTest {
    /**
     * Mock Bukkit server instance used for all tests.
     * <p>
     * Set up during {@link #globalSetUp()} and torn down during {@link #globalTearDown()}.
     * This allows tests to interact with Minecraft server objects without requiring an actual server.
     * </p>
     */
    static ServerMock mockServer;

    /**
     * The Ollivanders2 plugin instance loaded with default test configuration.
     * <p>
     * Initialized during {@link #globalSetUp()} to provide access to plugin functionality and
     * allow proper initialization of all O2Items through the plugin's startup sequence.
     * </p>
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test {@link O2WandWoodType#getMaterial()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getMaterialTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link O2WandWoodType#getLabel()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getLabelTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link O2WandWoodType#getWandWoodTypeByMaterial(Material)}.
     * <p>
     * Verifies that wand wood types can be correctly retrieved by {@link Material}.
     * </p>
     * <ul>
     *   <li>Invalid material (AIR): Returns null</li>
     *   <li>Valid wood material (CHERRY): Returns correct wood type</li>
     * </ul>
     */
    @Test
    void getWandWoodTypeByMaterialTest() {
        assertNull(O2WandWoodType.getWandWoodTypeByMaterial(Material.AIR), "O2WandWoodType.getWandWoodTypeByMaterial(Material.AIR) did not return null");

        O2WandWoodType expectedWoodType = O2WandWoodType.CHERRY;
        O2WandWoodType woodType = O2WandWoodType.getWandWoodTypeByMaterial(expectedWoodType.getMaterial());
        assertNotNull(woodType, "O2WandWoodType.getWandWoodTypeByMaterial(expectedWoodType.getMaterial()) returned null.");
        assertEquals(expectedWoodType, woodType, "O2WandWoodType.getWandWoodTypeByMaterial(expectedWoodType.getMaterial()) did not return expected type.");
    }

    /**
     * Test {@link O2WandWoodType#getWandWoodTypeByName(String)}.
     * <p>
     * Verifies that wand wood types can be correctly retrieved by name with exact case matching.
     * </p>
     * <ul>
     *   <li>Invalid name: Returns null</li>
     *   <li>Valid name (CHERRY): Returns correct wood type</li>
     *   <li>Case sensitivity: Lowercase name returns null</li>
     * </ul>
     */
    @Test
    void getWandWoodTypeByNameTest() {
        assertNull(O2WandWoodType.getWandWoodTypeByName("Invalid name"), "O2WandWoodType.getWandWoodTypeByName(\"Invalid name\") did not return null.");

        O2WandWoodType expectedWoodType = O2WandWoodType.CHERRY;
        O2WandWoodType woodType = O2WandWoodType.getWandWoodTypeByName(expectedWoodType.getLabel());
        assertNotNull(woodType, "O2WandWoodType.getWandWoodTypeByName(expectedWoodType.getLabel()) returned null.");
        assertEquals(expectedWoodType, woodType, "O2WandWoodType.getWandWoodTypeByName(expectedWoodType.getLabel()) did not return expected type.");

        // case sensitive - getWandWoodTypeByName uses equals(), not equalsIgnoreCase()
        woodType = O2WandWoodType.getWandWoodTypeByName(expectedWoodType.getLabel().toLowerCase());
        assertNull(woodType, "O2WandWoodType.getWandWoodTypeByName() should be case-sensitive but matched a lowercase name");
    }

    /**
     * Test {@link O2WandWoodType#getAllWandWoodsByName()}.
     * <p>
     * Verifies that the returned list contains all wand wood names with no duplicates.
     * </p>
     * <ul>
     *   <li>List is not empty and contains all enum values</li>
     *   <li>Each enum constant's label is present in the list</li>
     *   <li>No duplicate names exist in the list</li>
     * </ul>
     */
    @Test
    void getAllWandWoodsByNameTest() {
        ArrayList<String> wandWoods = O2WandWoodType.getAllWandWoodsByName();
        assertFalse(wandWoods.isEmpty());
        assertEquals(O2WandWoodType.values().length, wandWoods.size());

        for (O2WandWoodType wandWood : O2WandWoodType.values()) {
            assertTrue(wandWoods.contains(wandWood.getLabel()));
        }

        // verify no duplicate names
        Map<String, List<String>> grouped = wandWoods.stream()
                .collect(Collectors.groupingBy(name -> name));

        List<String> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate wand wood names: " + duplicates);
    }

    /**
     * Test {@link O2WandWoodType#getRandomWood()}.
     * <p>
     * Verifies that random wood selection returns valid wood types and exhibits variance
     * across multiple calls.
     * </p>
     * <ul>
     *   <li>All returned woods are valid and retrievable by name</li>
     *   <li>Multiple calls produce at least 2 different wood types (randomness verified)</li>
     * </ul>
     */
    @Test
    void getRandomWoodTest() {
        ArrayList<String> results = new ArrayList<>();

        // loop this many times so hopefully we catch enough variance
        for (int i = 0; i <= 30; i++) {
            String wandWood = O2WandWoodType.getRandomWood();
            O2WandWoodType wandWoodType = O2WandWoodType.getWandWoodTypeByName(wandWood);
            assertNotNull(wandWoodType, "getWandWoodTypeByName() returned null for getRandomWood() result: " + wandWood);
            results.add(wandWood);
        }

        // verify that randomness produces at least 2 different woods across 31 iterations
        long distinctCount = results.stream().distinct().count();
        assertTrue(distinctCount > 1, "getRandomWood() returned the same wood all 31 times: " + results.getFirst());
    }

    /**
     * Test {@link O2WandWoodType#getWandWoodBySeed(int)}.
     * <p>
     * Verifies that seed-based wood selection is deterministic and handles large seed values correctly.
     * </p>
     * <ul>
     *   <li>All returned woods are valid and retrievable by name for random seeds</li>
     *   <li>Same seed always produces the same wood (determinism verified with seed=42)</li>
     * </ul>
     */
    @Test
    void getWandWoodBySeedTest() {
        // loop this many times so hopefully we catch enough variance
        for (int i = 0; i <= 30; i++) {
            int seed = Math.abs(Ollivanders2Common.random.nextInt());

            String wandWood = O2WandWoodType.getWandWoodBySeed(seed);
            O2WandWoodType wandWoodType = O2WandWoodType.getWandWoodTypeByName(wandWood);
            assertNotNull(wandWoodType, "getWandWoodTypeByName() returned null for getWandWoodBySeed() result: " + wandWood);
        }

        // verify determinism - same seed should always produce same wood
        int fixedSeed = 42;
        String first = O2WandWoodType.getWandWoodBySeed(fixedSeed);
        for (int i = 0; i < 5; i++) {
            String result = O2WandWoodType.getWandWoodBySeed(fixedSeed);
            assertEquals(first, result, "getWandWoodBySeed() returned different results for the same seed");
        }
    }

    /**
     * Test {@link O2WandWoodType#isWandWood(Material)}.
     * <p>
     * Verifies that wood type detection correctly identifies wand woods.
     * </p>
     * <ul>
     *   <li>Valid wand wood material (BIRCH): Returns true</li>
     *   <li>Invalid material (AIR): Returns false</li>
     * </ul>
     */
    @Test
    void isWandWoodTest() {
        assertTrue(O2WandWoodType.isWandWood(O2WandWoodType.BIRCH.getMaterial()), "O2WandWoodType.isWandWood(O2WandWoodType.BIRCH.getMaterial()) returned false");

        assertFalse(O2WandWoodType.isWandWood(Material.AIR), "O2WandWoodType.isWandWood(Material.AIR) returned true");
    }

    /**
     * Reset test state after each test method.
     * <p>
     * Ensures the debug flag is disabled after each test to prevent debug output from affecting
     * subsequent tests or polluting test logs.
     * </p>
     */
    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    /**
     * Clean up MockBukkit server after all tests complete.
     * <p>
     * Releases MockBukkit resources and unloads the mock server. This must be called after all
     * tests in the class have finished to properly clean up the test environment and allow
     * other test classes to create fresh mock servers.
     * </p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
