package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BROOM_FLYING;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the BROOM_FLYING effect.
 *
 * <p>BroomFlyingTest validates that the BROOM_FLYING effect correctly enables flight for players
 * while riding brooms. This test extends {@link FlyingTest} to inherit flight-related test methods
 * while ensuring that BROOM_FLYING effects are always permanent (unlike regular FLYING effects).</p>
 *
 * @see BROOM_FLYING for the effect implementation being tested
 * @see FlyingTest for the base flying effect testing framework
 */
public class BroomFlyingTest extends FlyingTest {
    /**
     * Create a BROOM_FLYING effect for testing.
     *
     * <p>Creates a permanent BROOM_FLYING effect. The isPermanent parameter is ignored as
     * BROOM_FLYING effects are always permanent by design.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration parameter (unused for permanent effects)
     * @param isPermanent     ignored; BROOM_FLYING effects are always permanent
     * @return the newly created BROOM_FLYING effect instance
     */
    @Override
    BROOM_FLYING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BROOM_FLYING(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * Test that BROOM_FLYING effects are always permanent and cannot be changed.
     * <p>
     * This test overrides the parent {@link FlyingTest#isPermanentTest()} to validate BROOM_FLYING's
     * special behavior: unlike regular FLYING effects which can be temporary or permanent, BROOM_FLYING
     * effects are always permanent regardless of configuration. This is by design - once a player has
     * a broom with flight capability, the flight effect persists until explicitly removed.
     * </p>
     * <p>
     * Test flow:
     * <ul>
     * <li>Creates a BROOM_FLYING effect (which ignores the isPermanent parameter)</li>
     * <li>Verifies the effect reports as permanent via {@link O2Effect#isPermanent()}</li>
     * <li>Attempts to set the effect as non-permanent via {@link O2Effect#setPermanent(boolean)}</li>
     * <li>Verifies the effect still reports as permanent (ignores the change)</li>
     * </ul>
     * </p>
     * <p>
     * This test ensures BROOM_FLYING cannot be accidentally configured as a temporary effect,
     * maintaining the integrity of broom flight mechanics.
     * </p>
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
