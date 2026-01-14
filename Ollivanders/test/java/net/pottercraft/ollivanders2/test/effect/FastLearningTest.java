package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FAST_LEARNING;
import org.bukkit.entity.Player;

/**
 * Test suite for the FAST_LEARNING effect.
 *
 * <p>FAST_LEARNING is a passive marker effect that boosts skill experience gain.
 * This test validates basic effect creation, duration management, and lifecycle behavior.</p>
 */
public class FastLearningTest extends EffectTestSuper {
    /**
     * Create a FAST_LEARNING effect for testing.
     *
     * <p>Instantiates a new FAST_LEARNING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new FAST_LEARNING effect targeting the specified player
     */
    @Override
    FAST_LEARNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FAST_LEARNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * FAST_LEARNING effect does not have special checkEffect behavior.
     *
     * <p>The FAST_LEARNING effect has no unique behavior to test in checkEffectTest(). Aging and duration
     * management are tested in the base ageAndKillTest() method.</p>
     */
    @Override
    void checkEffectTest() {
    }

    /**
     * Run all event handler tests for the FAST_LEARNING effect.
     *
     * <p>FAST_LEARNING has no event handlers to test.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Test FAST_LEARNING effect cleanup.
     *
     * <p>FAST_LEARNING has no persistent state to clean up.</p>
     */
    @Override
    void doRemoveTest() {}
}
