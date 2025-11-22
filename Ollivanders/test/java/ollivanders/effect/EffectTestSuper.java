package ollivanders.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2Effect;
import ollivanders.testcommon.TestCommon;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class for testing O2Effect subclasses.
 *
 * <p>EffectTestSuper provides a common testing framework for all O2Effect implementations, handling
 * MockBukkit server setup and teardown, and providing abstract methods for concrete effect test classes
 * to implement. This class manages the lifecycle of test fixtures including the mock Bukkit server,
 * test plugin, test world, and target player that effects are applied to.</p>
 *
 * <p>Test fixtures are initialized before each test method: the MockBukkit server is created globally
 * once, the plugin is loaded with default configuration, a world is added, the scheduler is advanced by
 * the initial startup delay, and a test player is created. Each concrete subclass must implement
 * createEffect() to instantiate the specific effect type being tested.</p>
 *
 * <p>Core Behaviors Tested:</p>
 * <ul>
 * <li><strong>Constructor Validation:</strong> Effects initialize with correct duration and permanent status</li>
 * <li><strong>Duration Aging:</strong> Effects properly decrement duration and auto-kill when duration reaches zero</li>
 * <li><strong>Target ID Handling:</strong> Target player IDs are properly stored and returned</li>
 * <li><strong>Permanent Flag:</strong> Effects can be marked as permanent and duration changes are respected</li>
 * </ul>
 *
 * @author Azami7
 * @see O2Effect for the abstract base class being tested
 */
abstract public class EffectTestSuper {
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
    Ollivanders2 testPlugin;

    /**
     * The test world where effects are applied.
     *
     * <p>A simple world created for this test instance. Used to provide a world context for
     * operations that require world references.</p>
     */
    World testWorld;

    /**
     * The target player for effect testing.
     *
     * <p>A mock player created for each test method. This player is the target of all effects
     * created in the test, allowing effect-to-player interactions to be verified.</p>
     */
    PlayerMock target;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);

        // create the player this effect will target
        target = mockServer.addPlayer();
    }

    /**
     * Create the specific effect under test.
     *
     * <p>Abstract method that must be implemented by concrete test subclasses to instantiate the
     * specific O2Effect subclass being tested. This allows each test class to test a different
     * effect type while reusing the common testing framework.</p>
     *
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @param targetID        the unique ID of the player this effect will target
     * @return the newly created O2Effect instance of the type being tested
     */
    abstract O2Effect createEffect(int durationInTicks, boolean isPermanent, @NotNull UUID targetID);

    /**
     * Test effect constructor behavior and initial state.
     *
     * <p>Verifies that effects created via the constructor have the correct initial state:
     * duration is set to the minimum allowed duration (O2Effect.minDuration) when a small duration
     * is specified, and the effect is marked as permanent when isPermanent is true. Also verifies
     * that new effects are not in the killed state.</p>
     */
    @Test
    void constructorTest() {
        // create an effect that lasts 10 ticks
        O2Effect effect = createEffect(10, false, target.getUniqueId());
        assertFalse(effect.isKilled(), "Effect set to killed at creation");
        assertEquals(O2Effect.minDuration, effect.getRemainingDuration(), "Effect duration not set to minimum duration when duration specified as 10 in constructor");

        // create an effect with -1 duration, should be permanent
        effect = createEffect(5, true, target.getUniqueId());
        assertTrue(effect.isPermanent(), "Effect not permanent");
        assertFalse(effect.isKilled(), "Effect set to killed at creation");
    }

    /**
     * Test effect aging and killing behavior.
     *
     * <p>Verifies that effects properly handle duration aging and termination:
     * the kill() method sets the killed flag, the age() method decrements duration by the
     * specified amount, and duration going below zero automatically kills the effect.</p>
     */
    @Test
    void ageAndKillTest() {
        int duration = O2Effect.minDuration;
        O2Effect effect = createEffect(duration, false, target.getUniqueId());

        // kill() kills the effect
        effect.kill();
        assertTrue(effect.isKilled(), "Effect not killed after effect.kill();");

        // age decrements duration as expected
        effect = createEffect(duration, false, target.getUniqueId());
        effect.age(1);
        assertEquals(duration - 1, effect.getRemainingDuration(), "Age did not properly decrement effect duration");

        // age decrementing duration below 0 kills the effect
        effect.age(duration);
        assertTrue(effect.isKilled(), "Effect not set to killed when duration < 0");
    }

    /**
     * Test the getTargetID() method returns a copy of the target UUID.
     *
     * <p>Verifies that getTargetID() returns a copy of the internal UUID object rather than the
     * original reference. This is important for data encapsulation and preventing external code
     * from modifying the effect's internal state.</p>
     */
    @Test
    void getTargetIDTest() {
        UUID targetID = target.getUniqueId();
        O2Effect effect = createEffect(10, false, targetID);

        UUID id = effect.getTargetID();
        assertFalse(id == targetID, "effect.getTargetID() returned same UUID object");
    }

    /**
     * Test the permanent flag behavior.
     *
     * <p>Verifies that the permanent flag is correctly initialized and can be changed via
     * setPermanent(). Tests that new effects default to non-permanent, and that the flag can be
     * toggled between true and false states.</p>
     */
    @Test
    void isPermanentTest() {
        O2Effect effect = createEffect(10, false, target.getUniqueId());
        assertFalse(effect.isPermanent(), "Effect already set to permanent");

        effect.setPermanent(true);
        assertTrue(effect.isPermanent(), "Effect not permanent after effect.setPermanent(true);");

        effect.setPermanent(false);
        assertFalse(effect.isPermanent(), "Effect set to permanent after effect.setPermanent(false);");
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
