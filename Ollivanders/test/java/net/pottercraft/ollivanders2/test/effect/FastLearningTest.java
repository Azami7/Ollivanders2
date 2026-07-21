package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FAST_LEARNING;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link FAST_LEARNING}.
 *
 * @see EffectTestSuper
 */
public class FastLearningTest extends EffectTestSuper {
    @Override
    FAST_LEARNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FAST_LEARNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {
    }

    @Override
    void eventHandlerTests() {}

    @Override
    void doRemoveTest() {}
}
