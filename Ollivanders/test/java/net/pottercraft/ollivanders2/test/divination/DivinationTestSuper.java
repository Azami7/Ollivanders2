package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.divination.O2Divination;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for divination spell tests. Subclasses implement {@link #createDivination(Player, Player, int)} to supply
 * the spell under test and inherit the shared {@link #divineTest()} coverage.
 *
 * @see O2Divination
 */
abstract public class DivinationTestSuper {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, reloaded before each test method for isolation.
     */
    Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's 20-tick startup delay so the plugin is fully initialized
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Create the divination spell instance under test.
     *
     * @param prophet    the player casting the divination
     * @param target     the player the prophecy is about
     * @param experience the prophet's experience level; higher values make the prophecy more likely to succeed
     * @return a new divination instance of the type being tested
     */
    abstract O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience);

    /**
     * Divining with high experience adds exactly one prophecy naming the target.
     */
    @Test
    void divineTest() {
        List<String> prophecies = Ollivanders2API.getProphecies().getProphecies();
        assertEquals(0, prophecies.size(), "Tests did not start out with empty prophecies list.");

        PlayerMock prophet = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();

        O2Divination divination = createDivination(prophet, target, 100);
        divination.divine();

        mockServer.getScheduler().performTicks(10);

        prophecies = Ollivanders2API.getProphecies().getProphecies();
        assertEquals(1, prophecies.size(), "Number of prophecies is not 1 after O2Divination.divine()");
        assertTrue(TestCommon.containsStringMatch(prophecies, target.getName()), "Target player name not found in any prophecy.");
    }

    @AfterEach
    void tearDown() {
        Ollivanders2API.getProphecies().resetProphecies();
    }

    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
