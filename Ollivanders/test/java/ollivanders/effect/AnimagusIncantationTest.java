package ollivanders.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        int duration = 100;

        ANIMAGUS_INCANTATION incantation = createEffect(duration, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(incantation);

        // checkEffect() ages ANIMAGUS_INCANTATION by 1 every tick
        mockServer.getScheduler().performTicks(1);
        assertEquals(duration - 1, incantation.getRemainingDuration(), "ANIMAGUS_INCANTATION did not age after 1 tick");

        // ANIMAGUS_INCANTATION is killed when its duration ticks have passed
        mockServer.getScheduler().performTicks(duration);
        assertTrue(incantation.isKilled(), "ANIMAGUS_INCANTATION not killed after duration ticks have passed.");
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
