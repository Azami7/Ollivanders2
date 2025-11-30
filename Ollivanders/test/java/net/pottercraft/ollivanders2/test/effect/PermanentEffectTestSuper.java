package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Specialized test framework for permanent O2Effect subclasses.
 *
 * <p>PermanentEffectTestSuper extends EffectTestSuper to provide a testing framework specifically
 * for effect types that are always permanent and cannot have their permanent status changed.
 * These effects ignore the isPermanent parameter passed to constructors and always behave as permanent.</p>
 *
 * <p>The isPermanentTest() method is overridden to validate permanent effect semantics:
 * effects created with isPermanent=false are still permanent, and calling setPermanent(false)
 * has no effect on already-permanent effects.</p>
 *
 * @see EffectTestSuper for the base testing framework
 */
abstract public class PermanentEffectTestSuper extends EffectTestSuper {
    /**
     * Test that permanent effects cannot have their permanent status changed.
     *
     * <p>Verifies that permanent effects:</p>
     * <ul>
     * <li>Are created as permanent even when isPermanent parameter is false</li>
     * <li></li>Cannot be toggled to non-permanent via setPermanent(false)</li>
     * </ul>
     * <p>This override replaces the parent class test which validates toggling between permanent
     * and non-permanent states, since true permanent effects do not support this behavior.</p>
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
