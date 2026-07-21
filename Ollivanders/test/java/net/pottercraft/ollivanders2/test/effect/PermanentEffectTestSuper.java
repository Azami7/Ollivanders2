package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for tests of always-permanent {@link O2Effect} subclasses — effects that ignore the isPermanent
 * constructor argument and cannot be toggled off. Overrides {@link #isPermanentTest()} accordingly.
 *
 * @see EffectTestSuper
 */
abstract public class PermanentEffectTestSuper extends EffectTestSuper {
    /**
     * The effect is permanent even when created with isPermanent=false, and setPermanent(false) does not clear it.
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
