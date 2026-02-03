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
 * Abstract base class for testing Ollivanders2 divination spell implementations.
 * <p>
 * Provides a template for testing divination spells that generate prophecies about target players.
 * Subclasses must extend this class, implement the abstract {@link #createDivination(Player, Player, int)}
 * method to create the specific divination spell instance being tested, and optionally add divination-specific tests.
 * </p>
 * <p>
 * This class handles shared test infrastructure:
 * <ul>
 * <li>MockBukkit server initialization and cleanup ({@link #globalSetUp()}, {@link #globalTearDown()})</li>
 * <li>Plugin loading per test method ({@link #setUp()})</li>
 * <li>Prophecy state reset between tests ({@link #tearDown()})</li>
 * <li>Common divination validation test ({@link #divineTest()})</li>
 * </ul>
 * </p>
 * <p>
 * Test coverage includes:
 * <ul>
 * <li>Prophecy generation when divine() is called with experience-based accuracy</li>
 * <li>Target player name inclusion in generated prophecies</li>
 * <li>Prophecy list management (clean state between tests, proper addition of new prophecies)</li>
 * <li>Scheduler advancement for async prophecy generation (10 ticks)</li>
 * </ul>
 * </p>
 *
 * @see O2Divination the divination spell implementation being tested
 */
abstract public class DivinationTestSuper {
    /**
     * The MockBukkit server instance for this test class.
     * <p>
     * Initialized once per test class in {@link #globalSetUp()} and shared across all test methods.
     * Provides access to the mocked Bukkit server's scheduler and player management. Marked as {@code static}
     * since server initialization is expensive and should only happen once per test class execution.
     * </p>
     */
    static ServerMock mockServer;

    /**
     * The Ollivanders2 plugin instance loaded for this test.
     * <p>
     * Initialized fresh before each test method in {@link #setUp()} using the default plugin configuration.
     * Provides access to plugin APIs and Ollivanders2 functionality during tests. Marked as instance-level
     * (non-static) because plugins are reloaded before each test method to ensure clean test isolation.
     * </p>
     */
    Ollivanders2 testPlugin;

    /**
     * Initialize the MockBukkit server once for this entire test class.
     * <p>
     * This setup runs once per test class (via {@code @BeforeAll}) and initializes the MockBukkit server.
     * The server instance is stored in {@link #mockServer} for access by test setup methods and test cases.
     * </p>
     */
    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
    }

    /**
     * Load the Ollivanders2 plugin before each test method.
     * <p>
     * This setup runs before each test method (via {@code @BeforeEach}) and performs:
     * <ul>
     * <li>Loading Ollivanders2 plugin with default test configuration</li>
     * <li>Advancing the server scheduler by {@link TestCommon#startupTicks} (20 ticks) to allow the
     * plugin to complete its initialization (the scheduler has a built-in 20-tick startup delay)</li>
     * </ul>
     * This ensures each test starts with a fresh plugin instance in a fully initialized state.
     * </p>
     */
    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Template method for subclasses to create a specific divination spell instance.
     * <p>
     * Must be implemented by each concrete test subclass. The implementation should create and return
     * an O2Divination instance of the specific spell type being tested. For example:
     * </p>
     * <pre>
     * {@code
     * @Override
     * O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
     *     return new MyDivinationSpell(testPlugin, prophet, target, experience);
     * }
     * }
     * </pre>
     * <p>
     * This design follows the Template Method pattern, allowing each subclass to customize the divination
     * spell creation while using the common divination validation test in {@link #divineTest()}.
     * </p>
     *
     * @param prophet the player casting the divination spell
     * @param target the player who is the subject of the divination (prophecy will be about them)
     * @param experience the diviner's magical experience level, typically 0-100, affecting prophecy accuracy
     * @return a new O2Divination instance for the spell being tested
     */
    abstract O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience);

    /**
     * Test that a divination spell correctly generates and stores a prophecy.
     * <p>
     * This test validates the core divination functionality across all divination spell implementations.
     * Test flow:
     * <ul>
     * <li>Verify the prophecy list starts clean (empty state)</li>
     * <li>Create two mock players: a prophet (caster) and a target (subject of prophecy)</li>
     * <li>Create a divination spell instance with high experience (100) via {@link #createDivination(Player, Player, int)}</li>
     * <li>Execute the divination spell via {@link O2Divination#divine()}</li>
     * <li>Advance the server scheduler by 10 ticks to allow async prophecy generation</li>
     * <li>Verify exactly one prophecy was added to the system</li>
     * <li>Verify the target player's name appears somewhere in the generated prophecy</li>
     * </ul>
     * </p>
     * <p>
     * The high experience level (100) is used to ensure the divination succeeds reliably for testing.
     * In actual gameplay, lower experience levels might fail to generate a prophecy.
     * </p>
     */
    @Test
    void divineTest() {
        // make sure we're starting with a clean list
        List<String> prophecies = Ollivanders2API.getProphecies().getProphecies();
        assertEquals(0, prophecies.size(), "Tests did not start out with empty prophecies list.");

        // create a prophet and a target
        PlayerMock prophet = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();

        // do the divination with high experience
        O2Divination divination = createDivination(prophet, target, 100);
        divination.divine();

        // advance the game ticks
        mockServer.getScheduler().performTicks(10);

        // verify a prophecy was added
        prophecies = Ollivanders2API.getProphecies().getProphecies();
        assertEquals(1, prophecies.size(), "Number of prophecies is not 1 after O2Divination.divine()");
        assertTrue(TestCommon.containsStringMatch(prophecies, target.getName()), "Target player name not found in any prophecy.");
    }

    /**
     * Reset prophecy state after each test method.
     * <p>
     * This teardown runs after each test method (via {@code @AfterEach}) and clears all stored prophecies.
     * This ensures that prophecies generated in one test don't affect subsequent tests, maintaining
     * proper test isolation.
     * </p>
     */
    @AfterEach
    void tearDown() {
        Ollivanders2API.getProphecies().resetProphecies();
    }

    /**
     * Clean up the MockBukkit server after all tests in this test class have completed.
     * <p>
     * This teardown runs once per test class (via {@code @AfterAll}) and unmocks the MockBukkit server,
     * releasing all server resources and unloading all loaded plugins. This is necessary to prevent
     * resource leaks and interference between test classes when running the complete test suite.
     * </p>
     */
    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
