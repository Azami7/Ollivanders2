package net.pottercraft.ollivanders2.test.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
 * Test suite for {@link O2WandCoreType} enum functionality.
 * <p>
 * Verifies that wand core type retrieval, filtering, random selection, and item stack validation
 * work correctly across all core types (both legacy and non-legacy). Tests ensure deterministic
 * behavior for seed-based selection and proper distinction between legacy and non-legacy cores.
 * </p>
 */
public class O2WandCoreTypeTest {
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
     * Test {@link O2WandCoreType#getO2ItemType()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getO2ItemTypeTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link O2WandCoreType#getLabel()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getLabelTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link O2WandCoreType#getWandCoreTypeByItemType(O2ItemType)}.
     * <p>
     * Verifies that wand core types can be correctly retrieved by {@link O2ItemType}.
     * </p>
     * <ul>
     *   <li>Legacy core: GUNPOWDER is correctly retrieved</li>
     *   <li>Non-legacy core: PHOENIX_FEATHER is correctly retrieved</li>
     *   <li>Invalid type: BEZOAR returns null (not a wand core)</li>
     * </ul>
     */
    @Test
    void getWandCoreTypeByItemTypeTest() {
        // legacy core
        O2WandCoreType expected = O2WandCoreType.GUNPOWDER;
        O2WandCoreType wandCore = O2WandCoreType.getWandCoreTypeByItemType(O2ItemType.GUNPOWDER);
        assertNotNull(wandCore);
        assertEquals(expected, wandCore);

        // non-legacy core
        expected = O2WandCoreType.PHOENIX_FEATHER;
        wandCore = O2WandCoreType.getWandCoreTypeByItemType(O2ItemType.PHOENIX_FEATHER);
        assertNotNull(wandCore);
        assertEquals(expected, wandCore);

        // invalid type
        assertNull(O2WandCoreType.getWandCoreTypeByItemType(O2ItemType.BEZOAR));
    }

    /**
     * Test {@link O2WandCoreType#getWandCoreTypeByName(String)}.
     * <p>
     * Verifies that wand core types can be correctly retrieved by name with exact case matching.
     * </p>
     * <ul>
     *   <li>Valid legacy core: GUNPOWDER is correctly retrieved by name</li>
     *   <li>Valid non-legacy core: PHOENIX_FEATHER is correctly retrieved by name</li>
     *   <li>Invalid name: Returns null for non-existent core names</li>
     *   <li>Case sensitivity: Name matching is case-sensitive (lowercase returns null)</li>
     * </ul>
     */
    @Test
    void getWandCoreTypeByNameTest() {
        // valid legacy core
        O2WandCoreType expected = O2WandCoreType.GUNPOWDER;
        O2WandCoreType wandCore = O2WandCoreType.getWandCoreTypeByName(expected.getLabel());
        assertNotNull(wandCore, "getWandCoreTypeByName() returned null for valid legacy core");
        assertEquals(expected, wandCore, "getWandCoreTypeByName() returned wrong type for legacy core");

        // valid non-legacy core
        expected = O2WandCoreType.PHOENIX_FEATHER;
        wandCore = O2WandCoreType.getWandCoreTypeByName(expected.getLabel());
        assertNotNull(wandCore, "getWandCoreTypeByName() returned null for valid non-legacy core");
        assertEquals(expected, wandCore, "getWandCoreTypeByName() returned wrong type for non-legacy core");

        // invalid name
        wandCore = O2WandCoreType.getWandCoreTypeByName("Invalid Core");
        assertNull(wandCore, "getWandCoreTypeByName() did not return null for invalid name");

        // case sensitive - getWandCoreTypeByName uses equals(), not equalsIgnoreCase()
        wandCore = O2WandCoreType.getWandCoreTypeByName(expected.getLabel().toLowerCase());
        assertNull(wandCore, "getWandCoreTypeByName() should be case-sensitive but matched a lowercase name");
    }

    /**
     * Test {@link O2WandCoreType#getAllWandCoreNames()}.
     * <p>
     * Verifies that the returned list contains all wand core names with no duplicates.
     * </p>
     * <ul>
     *   <li>List is not empty and contains all enum values</li>
     *   <li>Each enum constant's label is present in the list</li>
     *   <li>No duplicate names exist in the list</li>
     * </ul>
     */
    @Test
    void getAllWandCoreNamesTest() {
        ArrayList<String> wandCores = O2WandCoreType.getAllWandCoreNames();
        assertFalse(wandCores.isEmpty());
        assertEquals(O2WandCoreType.values().length, wandCores.size());

        for (O2WandCoreType wandCore : O2WandCoreType.values()) {
            assertTrue(wandCores.contains(wandCore.getLabel()));
        }

        // verify no duplicate names
        Map<String, List<String>> grouped = wandCores.stream()
                .collect(Collectors.groupingBy(name -> name));

        List<String> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate wand core names: " + duplicates);
    }

    /**
     * Test {@link O2WandCoreType#getAllCoresByO2ItemType()}.
     * <p>
     * Verifies that the returned list contains all wand core item types with no duplicates.
     * </p>
     * <ul>
     *   <li>List is not empty and contains all enum values</li>
     *   <li>No duplicate O2ItemTypes exist in the list</li>
     * </ul>
     */
    @Test
    void getAllCoresByO2ItemTypeTest() {
        ArrayList<O2ItemType> wandCores = O2WandCoreType.getAllCoresByO2ItemType();
        assertFalse(wandCores.isEmpty());
        assertEquals(O2WandCoreType.values().length, wandCores.size());

        // verify no duplicate item types
        Map<O2ItemType, List<O2ItemType>> grouped = wandCores.stream()
                .collect(Collectors.groupingBy(itemType -> itemType));

        List<O2ItemType> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate O2ItemTypes in wandCores: " + duplicates);
    }

    /**
     * Test {@link O2WandCoreType#getRandomCore()}.
     * <p>
     * Verifies that random core selection never returns legacy cores and exhibits variance
     * across multiple calls.
     * </p>
     * <ul>
     *   <li>All returned cores are valid and non-legacy</li>
     *   <li>Multiple calls produce at least 2 different core types (randomness verified)</li>
     * </ul>
     */
    @Test
    void getRandomCoreTest() {
        ArrayList<String> results = new ArrayList<>();

        // loop this many times so hopefully we catch enough variance
        for (int i = 0; i <= 30; i++) {
            String wandCore = O2WandCoreType.getRandomCore();
            O2WandCoreType wandCoreType = O2WandCoreType.getWandCoreTypeByName(wandCore);
            assertNotNull(wandCoreType, "getWandCoreTypeByName() returned null for getRandomCore() result: " + wandCore);
            assertFalse(wandCoreType.isLegacy(), "O2WandCoreType.getRandomCore() returned a legacy core type");
            results.add(wandCore);
        }

        // verify that randomness produces at least 2 different cores across 31 iterations
        long distinctCount = results.stream().distinct().count();
        assertTrue(distinctCount > 1, "getRandomCore() returned the same core all 31 times: " + results.getFirst());
    }

    /**
     * Test {@link O2WandCoreType#getWandCoreBySeed(int)}.
     * <p>
     * Verifies that seed-based core selection is deterministic, never returns legacy cores,
     * and handles large seed values correctly.
     * </p>
     * <ul>
     *   <li>All returned cores are valid and non-legacy for random seeds</li>
     *   <li>Same seed always produces the same core (determinism verified with seed=42)</li>
     * </ul>
     */
    @Test
    void getWandCoreBySeedTest() {
        // loop this many times so hopefully we catch enough variance
        for (int i = 0; i <= 30; i++) {
            int seed = Math.abs(Ollivanders2Common.random.nextInt());

            String wandCore = O2WandCoreType.getWandCoreBySeed(seed);
            O2WandCoreType wandCoreType = O2WandCoreType.getWandCoreTypeByName(wandCore);
            assertNotNull(wandCoreType, "getWandCoreTypeByName() returned null for getWandCoreBySeed() result: " + wandCore);
            assertFalse(wandCoreType.isLegacy(), "O2WandCoreType.getWandCoreBySeed(seed) returned legacy core type " + wandCore);
        }

        // verify determinism - same seed should always produce same core
        int fixedSeed = 42;
        String first = O2WandCoreType.getWandCoreBySeed(fixedSeed);
        for (int i = 0; i < 5; i++) {
            String result = O2WandCoreType.getWandCoreBySeed(fixedSeed);
            assertEquals(first, result, "getWandCoreBySeed() returned different results for the same seed");
        }
    }

    /**
     * Test {@link O2WandCoreType#isWandCore(ItemStack)}.
     * <p>
     * Verifies that wand core detection correctly identifies O2Items that are wand cores.
     * The overloaded {@link O2WandCoreType#isWandCore(org.bukkit.entity.Item)} method is not tested separately as it
     * delegates directly to this method.
     * </p>
     * <ul>
     *   <li>Non-wand item (APPLE): Returns false</li>
     *   <li>Vanilla material that matches wand core (STRING): Returns false (not an O2Item)</li>
     *   <li>O2Item that is not a wand core (DITTANY): Returns false</li>
     *   <li>Vanilla BONE material: Returns false (must be O2Item)</li>
     *   <li>Legacy core O2Item (BONE): Returns true</li>
     *   <li>Non-legacy core O2Item (UNICORN_HAIR): Returns true</li>
     * </ul>
     */
    @Test
    void isWandCoreTest() {
        // test an item stack that is not a wand core
        ItemStack itemStack = new ItemStack(Material.APPLE, 1);
        assertFalse(O2WandCoreType.isWandCore(itemStack), "O2WandCoreType.isWandCore(itemStack) returned true for Material.APPLE");

        // test an item stack with a material that is a wand core but is not an O2Item
        itemStack = new ItemStack(Material.STRING, 1);
        assertFalse(O2WandCoreType.isWandCore(itemStack), "O2WandCoreType.isWandCore(itemStack) returned true for Material.STRING");

        // test an O2Item that is not a wand type
        itemStack = O2ItemType.DITTANY.getItem(1);
        assertFalse(O2WandCoreType.isWandCore(itemStack), "O2WandCoreType.isWandCore(itemStack) returned true for O2ItemType.DITTANY");

        // test legacy core
        itemStack = new ItemStack(Material.BONE);
        assertFalse(O2WandCoreType.isWandCore(itemStack), "O2WandCoreType.isWandCore(itemStack) returned true for Material.BONE");
        itemStack = O2ItemType.BONE.getItem(1);
        assertTrue(O2WandCoreType.isWandCore(itemStack), "O2WandCoreType.isWandCore(itemStack) returned false for O2ItemType.BONE");

        // test non-legacy core
        itemStack = O2ItemType.UNICORN_HAIR.getItem(1);
        assertTrue(O2WandCoreType.isWandCore(itemStack), "O2WandCoreType.isWandCore(itemStack) returned false for O2ItemType.UNICORN_HAIR");
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
