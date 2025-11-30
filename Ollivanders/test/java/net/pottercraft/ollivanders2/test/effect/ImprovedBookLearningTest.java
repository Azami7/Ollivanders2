package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.IMPROVED_BOOK_LEARNING;
import org.bukkit.entity.Player;

/**
 * Test suite for the IMPROVED_BOOK_LEARNING effect.
 *
 * <p>IMPROVED_BOOK_LEARNING is a passive marker effect that boosts book learning effectiveness.
 * This test validates basic effect creation, duration management, and lifecycle behavior.</p>
 */
public class ImprovedBookLearningTest extends EffectTestSuper {
    /**
     * Create an IMPROVED_BOOK_LEARNING effect for testing.
     *
     * <p>Instantiates a new IMPROVED_BOOK_LEARNING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new IMPROVED_BOOK_LEARNING effect targeting the specified player
     */
    @Override
    IMPROVED_BOOK_LEARNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new IMPROVED_BOOK_LEARNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic IMPROVED_BOOK_LEARNING effect behavior.
     *
     * <p>IMPROVED_BOOK_LEARNING is a passive marker effect with no active behavior. This test validates
     * that the effect properly ages and expires without side effects.</p>
     */
    @Override
    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    /**
     * Run all event handler tests for the IMPROVED_BOOK_LEARNING effect.
     *
     * <p>IMPROVED_BOOK_LEARNING has no event handlers to test.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Test IMPROVED_BOOK_LEARNING effect cleanup.
     *
     * <p>IMPROVED_BOOK_LEARNING has no persistent state to clean up.</p>
     */
    @Override
    void doRemoveTest() {}
}
