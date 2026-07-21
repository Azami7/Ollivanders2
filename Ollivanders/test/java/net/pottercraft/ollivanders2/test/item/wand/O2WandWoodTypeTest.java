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
 * Unit tests for {@link O2WandWoodType}.
 */
public class O2WandWoodTypeTest {
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
     * {@link O2WandWoodType#getMaterial()} is a simple getter and needs no explicit test.
     */
    @Test
    void getMaterialTest() {
        // simple getter, so we can skip this test
    }

    /**
     * {@link O2WandWoodType#getLabel()} is a simple getter and needs no explicit test.
     */
    @Test
    void getLabelTest() {
        // simple getter, so we can skip this test
    }

    /**
     * getWandWoodTypeByMaterial() returns the wood type for a wand-wood material, and null for a non-wand material.
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
     * getWandWoodTypeByName() matches by exact (case-sensitive) name, and returns null for an unknown or wrong-case name.
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
     * getAllWandWoodsByName() returns every wood's name with no duplicates.
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
     * getRandomWood() returns valid wood names and varies across repeated calls.
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
     * getWandWoodBySeed() returns a valid wood and is deterministic for a given seed.
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
     * isWandWood() is true for a wand-wood material and false otherwise.
     */
    @Test
    void isWandWoodTest() {
        assertTrue(O2WandWoodType.isWandWood(O2WandWoodType.BIRCH.getMaterial()), "O2WandWoodType.isWandWood(O2WandWoodType.BIRCH.getMaterial()) returned false");

        assertFalse(O2WandWoodType.isWandWood(Material.AIR), "O2WandWoodType.isWandWood(Material.AIR) returned true");
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
