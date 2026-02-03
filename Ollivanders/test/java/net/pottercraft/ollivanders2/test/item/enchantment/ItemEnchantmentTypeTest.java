package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link ItemEnchantmentType} enum functionality.
 * <p>
 * Verifies that enchantment type accessors work correctly and that enchantment names are unique
 * across all defined enchantment types.
 * </p>
 */
public class ItemEnchantmentTypeTest {
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
     * Test {@link ItemEnchantmentType#getName()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getNameTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link ItemEnchantmentType#getClassName()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getClassNameTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link ItemEnchantmentType#getLore()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getLoreTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link ItemEnchantmentType#getLevel()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void getLevelTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link ItemEnchantmentType#isCursed()}.
     * <p>
     * This is a simple accessor method, so no explicit test is needed.
     * </p>
     */
    @Test
    void isCursedTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test that all enchantment names are unique.
     * <p>
     * Verifies that no two {@link ItemEnchantmentType} enum constants share the same name,
     * which is required for proper enchantment identification and lookup.
     * </p>
     * <ul>
     *   <li>All enchantment names are unique (no duplicates)</li>
     * </ul>
     */
    @Test
    void uniqueEnchantmentNameTest() {
        Map<String, List<ItemEnchantmentType>> grouped = Arrays.stream(ItemEnchantmentType.values())
                .collect(Collectors.groupingBy(ItemEnchantmentType::getName));

        List<String> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate item names: " + duplicates);
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
