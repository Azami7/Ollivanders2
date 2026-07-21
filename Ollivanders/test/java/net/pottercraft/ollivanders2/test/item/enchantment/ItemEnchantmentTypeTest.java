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
 * Unit tests for {@link ItemEnchantmentType}.
 */
public class ItemEnchantmentTypeTest {
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

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * getName() is a simple accessor, so it needs no separate test.
     */
    @Test
    void getNameTest() {
        // simple getter, so we can skip this test
    }

    /**
     * getClassName() is a simple accessor, so it needs no separate test.
     */
    @Test
    void getClassNameTest() {
        // simple getter, so we can skip this test
    }

    /**
     * getLore() is a simple accessor, so it needs no separate test.
     */
    @Test
    void getLoreTest() {
        // simple getter, so we can skip this test
    }

    /**
     * getLevel() is a simple accessor, so it needs no separate test.
     */
    @Test
    void getLevelTest() {
        // simple getter, so we can skip this test
    }

    /**
     * isCursed() is a simple accessor, so it needs no separate test.
     */
    @Test
    void isCursedTest() {
        // simple getter, so we can skip this test
    }

    /**
     * No two enchantment types share a name, so lookup by name is unambiguous.
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

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
