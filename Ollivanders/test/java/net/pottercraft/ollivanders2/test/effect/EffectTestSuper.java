package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

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
     * Origin location for spatial tests.
     *
     * <p>Provides a default location at coordinates (0, 4, 0) in the test world.
     * Used by tests that require location context for spawning entities or validating
     * position-based effect behaviors.</p>
     */
    Location origin;

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
    }

    /**
     * Initialize test fixtures before each test method.
     *
     * <p>Creates fresh test fixtures for each test: loads the plugin with default configuration,
     * creates a test world, advances the scheduler past the initial startup delay, and creates
     * a test player to be the target of effects.</p>
     */
    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);

        origin = new Location(testWorld, 0, 4, 0);
    }

    /**
     * Create the specific effect under test.
     *
     * <p>Abstract method that must be implemented by concrete test subclasses to instantiate the
     * specific O2Effect subclass being tested. This allows each test class to test a different
     * effect type while reusing the common testing framework.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return the newly created O2Effect instance of the type being tested
     */
    abstract O2Effect createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Helper method to create an effect, add it to the effect manager, and process one tick.
     *
     * <p>This method simplifies test setup by combining effect creation, registration, and initial
     * processing into a single call. The effect is added to the player effect manager and then
     * advanced by one tick to ensure it's fully processed into the active effects system.</p>
     *
     * @param target      the player to add the effect to
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent true if the effect should be permanent, false for limited duration
     * @return the created and registered effect
     */
    O2Effect addEffect(Player target, int duration, boolean isPermanent) {
        O2Effect effect = createEffect(target, duration, isPermanent);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // Perform one tick to ensure the effect is processed into the active effects system
        mockServer.getScheduler().performTicks(1);

        return effect;
    }

    /**
     * Comprehensive effect test combining all core behavior validations.
     *
     * <p>This is a mega test that runs all effect tests in sequence within a single @Test method.
     * This approach is necessary because parallelized, random-order JUnit tests mess up the shared
     * game state (MockBukkit server). By running all tests in order within effectTest(), we ensure
     * the game state remains consistent throughout all validations.</p>
     *
     * <p>Tests performed in sequence:</p>
     * <ul>
     * <li>Constructor validation with short duration and minimum duration enforcement</li>
     * <li>Constructor validation with permanent effects</li>
     * <li>Effect aging and killing behavior</li>
     * <li>Target ID copy semantics</li>
     * <li>Permanent flag initialization and toggling</li>
     * <li>Effect-specific behavior (implemented by subclasses)</li>
     * <li>Effect-specific cleanup behavior (implemented by subclasses)</li>
     * </ul>
     */
    @Test
    void effectTest() {
        Ollivanders2.debug = true;
        // create an effect that lasts 10 ticks
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertFalse(effect.isKilled(), "Effect set to killed at creation");

        // create an effect with is permanent set true
        effect = createEffect(mockServer.addPlayer(), 5, true);
        assertFalse(effect.isKilled(), "Effect set to killed at creation");

        durationBoundsTest();

        ageAndKillTest();

        getTargetIDTest();

        isPermanentTest();

        checkEffectTest();

        eventHandlerTests();

        doRemoveTest();

        Ollivanders2.debug = false;
    }

    /**
     * Test that effect duration is properly bounded by minimum and maximum duration constraints.
     *
     * <p>Validates that when an effect is created with a duration below the minimum, it is clamped
     * to the minimum duration. Similarly, when created with a duration above the maximum, it is
     * clamped to the maximum duration. This ensures effects never have invalid duration values.</p>
     */
    void durationBoundsTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertEquals(effect.getMinDuration(), effect.getRemainingDuration(), "Effect duration not set to minimum duration when duration specified as 10 in constructor");

        // max duration is 1 hour, at 20 ticks per second, or 72000 ticks
        effect = createEffect(mockServer.addPlayer(), 72100, false);
        assertEquals(effect.getMaxDuration(), effect.getRemainingDuration(), "Effect duration not set to maximum duration when duration greater than maxDuration in constructor");
    }

    /**
     * Test effect aging and killing behavior.
     *
     * <p>Verifies that effects properly handle duration aging and termination:
     * the kill() method sets the killed flag, the age() method decrements duration by the
     * specified amount, and duration going below zero automatically kills the effect.</p>
     */
    void ageAndKillTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 100, false);
        int duration = effect.getMinDuration();

        // kill() kills the effect
        effect.kill();
        assertTrue(effect.isKilled(), "Effect not killed after effect.kill();");

        // age decrements duration as expected when the effect is not permanent
        if (!effect.isPermanent()) {
            effect = createEffect(mockServer.addPlayer(), duration, false);
            effect.age(1);
            assertEquals(duration - 1, effect.getRemainingDuration(), "Age did not properly decrement effect duration");

            // age decrementing duration below 0 kills the effect
            effect.age(duration);
            assertTrue(effect.isKilled(), "Effect not set to killed when duration < 0");
        }

        // make sure that non-permanent effects properly age as the game loop runs
        checkEventAging();
    }

    /**
     * Test the getTargetID() method returns a copy of the target UUID.
     *
     * <p>Verifies that getTargetID() returns a copy of the internal UUID object rather than the
     * original reference. This is important for data encapsulation and preventing external code
     * from modifying the effect's internal state.</p>
     */
    void getTargetIDTest() {
        Player target = mockServer.addPlayer();

        UUID targetID = target.getUniqueId();
        O2Effect effect = createEffect(target, 10, false);

        UUID id = effect.getTargetID();
        // we use == comparator here, not .equals() or assertNotSame, because we want to compare the object pointers not the values
        assertFalse(id == targetID, "effect.getTargetID() returned same UUID object");
    }

    /**
     * Test the permanent flag behavior.
     *
     * <p>Verifies that the permanent flag is correctly initialized and can be changed via
     * setPermanent(). Tests that new effects default to non-permanent, and that the flag can be
     * toggled between true and false states.</p>
     */
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);

        effect.setPermanent(true);
        assertTrue(effect.isPermanent(), "Effect not permanent after effect.setPermanent(true);");

        effect.setPermanent(false);
        assertFalse(effect.isPermanent(), "Effect set to permanent after effect.setPermanent(false);");
    }

    /**
     * Effect-specific behavior test to be implemented by subclasses.
     *
     * <p>Abstract method that subclasses must implement to test effect-specific behaviors beyond
     * the core O2Effect functionality. This is where effect-specific mechanics like potion application,
     * entity targeting, or other custom behaviors should be tested.</p>
     */
    abstract void checkEffectTest();

    /**
     * Effect-specific event handler test to be implemented by subclasses.
     *
     * <p>Abstract method that subclasses must implement to test that event handlers work correctly
     * for the specific effect type. If the effect does not handle any events, the implementation
     * should be empty but still present to satisfy the abstract contract.</p>
     */
    abstract void eventHandlerTests();

    /**
     * Effect-specific cleanup test to be implemented by subclasses.
     *
     * <p>Abstract method that subclasses must implement to test that doRemove() cleanup logic works
     * correctly for the specific effect type. If the effect has no special cleanup, the implementation
     * should be empty but still present to satisfy the abstract contract.</p>
     */
    abstract void doRemoveTest();

    /**
     * Test that aging correctly happens
     */
    void checkEventAging() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // advance the game ticks so that immediate effects which are then killed are completed
        mockServer.getScheduler().performTicks(5);

        if (!effect.isKilled() && !effect.isPermanent()) {
            // checkEffect() ages effect by 1 every tick
            int duration = effect.getRemainingDuration();
            mockServer.getScheduler().performTicks(1);
            assertEquals(duration - 1, effect.getRemainingDuration(), effect.effectType.toString() + " did not age after 1 tick");

            // effect is killed when its duration ticks have passed
            mockServer.getScheduler().performTicks(duration);
            assertTrue(effect.isKilled(), effect.effectType.toString() + " not killed after duration ticks have passed.");
        }
    }

    /**
     * Reset effect system state after each test.
     *
     * <p>Called after each test method to clean up state. Currently disables debug mode that may
     * have been enabled during testing.</p>
     */
    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
