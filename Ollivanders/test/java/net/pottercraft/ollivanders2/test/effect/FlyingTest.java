package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.FLYING;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the FLYING effect.
 *
 * <p>FlyingTest validates that the FLYING effect correctly enables flight for players while
 * the effect is active and properly disables it on the final tick. This test verifies that
 * flight permissions are managed correctly for both regular players and admins.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Flight is enabled for the target player when the effect is applied</li>
 * <li>Flight is disabled for regular players when the effect expires</li>
 * <li>Flight is preserved for admin players when the effect is removed</li>
 * <li>The effect properly integrates with the effect manager system</li>
 * </ul>
 *
 * @author Azami7
 * @see FLYING for the effect implementation being tested
 * @see EffectTestSuper for the base testing framework
 */
public class FlyingTest extends EffectTestSuper {
    /**
     * Create a FLYING effect for testing.
     *
     * <p>Instantiates a new FLYING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new FLYING effect targeting the specified player
     */
    FLYING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FLYING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Flying effect check. There is no way with MockBukkit to test for spawned particles so we cannot test that part.
     */
    void checkEffectTest() {
        Ollivanders2.debug = true;

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

    /**
     * FLYING effect does not handle events.
     */
    void eventHandlerTests() {
    }

    /**
     * Tests for the doRemove() cleanup. checkEffectTest() already does this for non-admins so this test just needs to test for admins.
     */
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
