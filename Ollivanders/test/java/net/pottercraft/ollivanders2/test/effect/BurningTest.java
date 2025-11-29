package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BURNING;
import org.bukkit.entity.Player;

/**
 * Test suite for the BURNING effect.
 *
 * <p>BURNING is an effect that sets players on fire, dealing damage over time. This test
 * validates basic effect creation, duration management, and lifecycle behavior.</p>
 */
public class BurningTest extends EffectTestSuper {
    /**
     * Create a BURNING effect for testing.
     *
     * <p>Instantiates a new BURNING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new BURNING effect targeting the specified player
     */
    BURNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BURNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic BURNING effect behavior.
     *
     * <p>BURNING has no special checkEffect behavior beyond base O2Effect.</p>
     */
    void checkEffectTest() {}

    /**
     * Run all event handler tests for the BURNING effect.
     *
     * <p>BURNING has no event handlers to test.</p>
     */
    void eventHandlerTests() {}

    /**
     * Test BURNING effect cleanup.
     *
     * <p>doRemove for BURNING doesn't do anything.</p>
     */
    void doRemoveTest() {}
}
