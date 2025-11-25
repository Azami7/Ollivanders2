package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;

/**
 * Test suite for the ANIMAGUS_INCANTATION effect.
 *
 * <p>Tests the incantation marker effect that temporarily flags a player as actively reciting
 * the Animagus spell. Verifies that the effect cannot be set to permanent status and that
 * it properly functions as a passive state marker without active tick processing or event handling.</p>
 */
public class AnimagusIncantationTest extends NotPermanentEffectTestSuper {
    ANIMAGUS_INCANTATION createEffect(int durationInTicks, boolean isPermanent) {
        return new ANIMAGUS_INCANTATION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    /**
     * ANIMAGUS_INCANTATION has no event handlers
     */
    void eventHandlerTests() {}

    /**
     * doRemove for ANIMAGUS_INCANTATION doesn't do anything
     */
    void doRemoveTest() {}
}
