package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.IMPROVED_BOOK_LEARNING;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link IMPROVED_BOOK_LEARNING}, a passive marker effect that boosts book-learning effectiveness.
 *
 * @see EffectTestSuper
 */
public class ImprovedBookLearningTest extends EffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    IMPROVED_BOOK_LEARNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new IMPROVED_BOOK_LEARNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {
    }

    @Override
    void eventHandlerTests() {}

    @Override
    void doRemoveTest() {}
}
