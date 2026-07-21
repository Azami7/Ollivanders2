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
 * Unit tests for {@link O2PotionType}.
 */
public class O2PotionTypeTest {
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
     * getClassName() is a simple getter, verified by potion instantiation tests.
     */
    @Test
    void getClassNameTest() {
        // simple getter, verified by potion instantiation tests
    }

    /**
     * getLevel() is a simple getter, verified by potion brewing tests.
     */
    @Test
    void getLevelTest() {
        // simple getter, verified by potion brewing tests
    }

    /**
     * getPotionName() is a simple getter, verified by getPotionTypeByName tests.
     */
    @Test
    void getPotionNameTest() {
        // simple getter, verified by getPotionTypeByName tests
    }

    /**
     * getPotionTypeFromString() parses enum name strings and returns null for an unknown name.
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
     * getPotionTypeByName() finds potions by display name, case-insensitively, including names with punctuation such as
     * "Baruffio's Brain Elixir"; unlike getPotionTypeFromString() which matches enum names.
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
     * Every potion type has a unique display name; duplicates would make getPotionTypeByName() ambiguous.
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

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
