package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Abstract test base class for effects that cannot ever be permanent.
 *
 * <p>Extends {@link EffectTestSuper} to provide common test infrastructure for effect classes that
 * enforce non-permanent behavior. These effects override or ignore the permanent flag and cannot be
 * set to permanent status, regardless of direct setPermanent() calls.</p>
 *
 * <p>Subclasses should implement the abstract methods from {@link EffectTestSuper} to define
 * effect-specific test setup and validation.</p>
 */
abstract public class NotPermanentEffectTestSuper extends EffectTestSuper {
    /**
     * Tests that effects enforcing non-permanent behavior cannot be set to permanent.
     *
     * <p>Verifies two key behaviors:
     * <ol>
     * <li>Effect is not permanent when initially created</li>
     * <li>Effect remains non-permanent even after calling setPermanent(true)</li>
     * </ol></p>
     *
     * <p>This test ensures that effect subclasses properly enforce their non-permanent constraint
     * and do not allow external code to override this restriction.</p>
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(10, true);
        assertFalse(effect.isPermanent(), "Effect permanent when created;");

        effect.setPermanent(true);
        assertFalse(effect.isPermanent(), "Non-permanent effect was permanent after effect.setPermanent(true)");
    }
}
