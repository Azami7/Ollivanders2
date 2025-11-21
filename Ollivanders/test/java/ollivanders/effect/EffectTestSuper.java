package ollivanders.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2Effect;
import ollivanders.testcommon.TestCommon;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

abstract public class EffectTestSuper {
    static ServerMock mockServer;
    Ollivanders2 testPlugin;
    World testWorld;
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
     * Create the specific effect under test - to be overridden by the child classes
     *
     * @param durationInTicks
     * @param targetID
     * @return
     */
    abstract O2Effect createEffect(int durationInTicks, boolean isPermanent, @NotNull UUID targetID);

    @Test
    void constructorTest() {
        // create an effect that lasts 10 ticks
        O2Effect effect = createEffect(10, false, target.getUniqueId());
        assertFalse(effect.isKilled(), "Effect set to killed at creation");
        assertEquals(O2Effect.minDuration, effect.getRemainingDuration(), "Effect duration not set to minimum duration when duration specified as 10 in constructor");

        // create an effect with -1 duration, should be permanent
        effect = createEffect(0, true, target.getUniqueId());
        assertTrue(effect.isPermanent(), "Effect not permanent");
        assertFalse(effect.isKilled(), "Effect set to killed at creation");
    }

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
     * This is not a simple getter because it makes a copy of the object so we need a test.
     */
    @Test
    void getTargetIDTest() {
        UUID targetID = target.getUniqueId();
        O2Effect effect = createEffect(10, false, targetID);

        UUID id = effect.getTargetID();
        assertFalse(id == targetID, "effect.getTargetID() returned same UUID object");
    }

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
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
