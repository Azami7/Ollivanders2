package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FLAGRANTE_BURNING;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the FLAGRANTE_BURNING effect.
 *
 * <p>FLAGRANTE_BURNING is an effect that sets players on fire, dealing damage over time. This test
 * validates basic effect creation, duration management, and lifecycle behavior.</p>
 */
public class FlagranteBurningTest extends BurningTest {
    /**
     * Create a FLAGRANTE_BURNING effect for testing.
     *
     * <p>Instantiates a new FLAGRANTE_BURNING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     ignored - flagrante burning is always permanent
     * @return a new FLAGRANTE_BURNING effect targeting the specified player
     */
    @Override
    FLAGRANTE_BURNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FLAGRANTE_BURNING(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * Test that flagrante burning effects cannot be changed from permanent status.
     *
     * <p>Validates that FLAGRANTE_BURNING effects are always permanent regardless of how they
     * are created or how the setPermanent() method is called. This test verifies the key invariant
     * of flagrante burning: it is an immutable permanent effect that persists until manually removed.</p>
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
