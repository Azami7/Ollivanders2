package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test base for effects that can never be permanent, even when constructed or set permanent.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
abstract public class NotPermanentEffectTestSuper extends EffectTestSuper {
    /**
     * The effect is not permanent when created permanent, and stays non-permanent after setPermanent(true).
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, true);
        assertFalse(effect.isPermanent(), "Effect permanent when created;");

        effect.setPermanent(true);
        assertFalse(effect.isPermanent(), "Non-permanent effect was permanent after effect.setPermanent(true)");
    }
}
