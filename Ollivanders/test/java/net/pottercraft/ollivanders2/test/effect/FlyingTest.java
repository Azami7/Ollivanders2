package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FLYING;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link FLYING}.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
public class FlyingTest extends EffectTestSuper {
    @Override
    FLYING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FLYING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Flight is enabled when the effect is added and, for non-admins, removed when it ages out. MockBukkit cannot
     * observe spawned particles, so that behavior is not tested.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        FLYING flying = (FLYING) addEffect(target, 100, false);
        int duration = flying.getRemainingDuration();

        assertTrue(target.getAllowFlight(), "Target not set to flying when effect added.");

        // verify effect killed and flight removed when duration ticks passed, if not permanent
        if (!flying.isPermanent()) {
            // Run the server forward until the effect ages out.
            mockServer.getScheduler().performTicks(duration);

            assertTrue(flying.isKilled(), "Flying effect not killed when duration ticks passed.");

            // verify AllowFlight removed
            assertFalse(target.getAllowFlight(), "Target flight not turned off when effect aged out.");
        }
    }

    @Override
    void eventHandlerTests() {
    }

    /**
     * doRemove() leaves an admin's flight enabled. checkEffectTest() already covers the non-admin case.
     */
    @Override
    void doRemoveTest() {
        // make an admin
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);

        FLYING flying = (FLYING) addEffect(admin, 10, false);

        // verify they are flying
        assertTrue(admin.getAllowFlight(), "Admin not set to flying when effect added.");

        // call doRemove() on the effect
        flying.doRemove();

        // verify admin is still flying
        assertTrue(admin.getAllowFlight(), "Admin player no longer flying after doRemove().");
    }
}
