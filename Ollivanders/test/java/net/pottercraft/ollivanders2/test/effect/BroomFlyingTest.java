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
    BROOM_FLYING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BROOM_FLYING(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created;");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
