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
 * Unit tests for {@link O2WandCoreType}.
 */
public class O2WandCoreTypeTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
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
     * {@link O2WandCoreType#getO2ItemType()} is a simple getter and needs no explicit test.
     */
    @Test
    void getO2ItemTypeTest() {
        // simple getter, so we can skip this test
    }

    /**
     * {@link O2WandCoreType#getLabel()} is a simple getter and needs no explicit test.
     */
    @Test
    void getLabelTest() {
        // simple getter, so we can skip this test
    }

    /**
     * getWandCoreTypeByItemType() returns the core type for a core item type, and null for a non-core item type.
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
     * getWandCoreTypeByName() matches by exact (case-sensitive) name, and returns null for an unknown or wrong-case name.
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
     * getAllWandCoreNames() returns every core's name with no duplicates.
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
     * getAllCoresByO2ItemType() returns every core's item type with no duplicates.
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
     * getRandomCore() returns valid non-legacy cores and varies across repeated calls.
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
     * getWandCoreBySeed() returns a valid non-legacy core and is deterministic for a given seed.
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
     * isWandCore() is true only for an O2Item core (legacy or not), and false for vanilla items or non-core O2Items —
     * a matching vanilla material alone is not enough. The Item overload delegates here and is not tested separately.
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

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
