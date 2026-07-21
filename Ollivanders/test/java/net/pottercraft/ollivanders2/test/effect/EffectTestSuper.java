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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for {@link O2Effect} tests. Subclasses implement {@link #createEffect(Player, int, boolean)} to supply the
 * effect under test plus its {@link #checkEffectTest()}, {@link #eventHandlerTests()}, and {@link #doRemoveTest()}
 * specifics, and inherit the shared core-behavior coverage in {@link #effectTest()}.
 *
 * @author Azami7
 * @see O2Effect
 */
abstract public class EffectTestSuper {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, reloaded before each test method for isolation.
     */
    Ollivanders2 testPlugin;

    /**
     * The test world effects are applied in.
     */
    World testWorld;

    /**
     * A default location, (0, 4, 0) in {@link #testWorld}, for tests that need a position.
     */
    Location origin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);

        origin = new Location(testWorld, 0, 4, 0);
    }

    /**
     * Create the effect under test.
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new effect of the type being tested
     */
    abstract O2Effect createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Create the effect, register it with the player effect manager, and process one tick so it becomes active.
     *
     * @param target      the player to add the effect to
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent true if the effect should be permanent, false for limited duration
     * @return the created and registered effect
     */
    O2Effect addEffect(Player target, int duration, boolean isPermanent) {
        O2Effect effect = createEffect(target, duration, isPermanent);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        mockServer.getScheduler().performTicks(1);

        return effect;
    }

    /**
     * Run the core effect behaviors plus the subclass-specific checks in sequence. Written as one method rather than
     * many because the scenarios share the mutable MockBukkit server state and JUnit does not guarantee method order.
     */
    @Test
    void effectTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);

        // create an effect that lasts 10 ticks
        O2Effect effect = createEffect(target, 10, false);
        assertFalse(effect.isKilled(), "Effect set to killed at creation");

        PlayerMock target2 = mockServer.addPlayer();
        target2.setLocation(origin);
        // create an effect with is permanent set true
        effect = createEffect(target2, 5, true);
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
     * A duration below the minimum or above the maximum is clamped into the effect type's bounds.
     */
    void durationBoundsTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);

        O2Effect effect = createEffect(target, 10, false);
        assertEquals(effect.getMinDuration(), effect.getRemainingDuration(), "Effect duration not set to minimum duration when duration specified as 10 in constructor");

        PlayerMock target2 = mockServer.addPlayer();
        target2.setLocation(origin);
        // max duration is 1 hour, at 20 ticks per second, or 72000 ticks
        effect = createEffect(target2, 72100, false);
        assertEquals(effect.getMaxDuration(), effect.getRemainingDuration(), "Effect duration not set to maximum duration when duration greater than maxDuration in constructor");
    }

    /**
     * kill() marks the effect killed, age() decrements duration, and aging past zero kills a non-permanent effect.
     */
    void ageAndKillTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);
        O2Effect effect = createEffect(target, 100, false);
        int duration = effect.getMinDuration();

        // kill() kills the effect
        effect.kill();
        assertTrue(effect.isKilled(), "Effect not killed after effect.kill();");
        mockServer.getScheduler().performTicks(20);

        // age decrements duration as expected when the effect is not permanent
        if (!effect.isPermanent()) {
            effect = createEffect(target, duration, false);
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
     * getTargetID() returns a copy of the target UUID, not the internal reference.
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
     * setPermanent() toggles the permanent flag both on and off.
     */
    void isPermanentTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);
        O2Effect effect = createEffect(target, 10, false);

        effect.setPermanent(true);
        assertTrue(effect.isPermanent(), "Effect not permanent after effect.setPermanent(true);");

        effect.setPermanent(false);
        assertFalse(effect.isPermanent(), "Effect set to permanent after effect.setPermanent(false);");
    }

    /**
     * Test the effect's own tick behavior; empty if it has none.
     */
    abstract void checkEffectTest();

    /**
     * Test the effect's event handlers; empty if it handles no events.
     */
    abstract void eventHandlerTests();

    /**
     * Test the effect's doRemove() cleanup; empty if it has none.
     */
    abstract void doRemoveTest();

    /**
     * A non-permanent effect ages by one per tick as the game loop runs and is killed once its duration elapses.
     */
    void checkEventAging() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);

        O2Effect effect = createEffect(target, 100, false);
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

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
