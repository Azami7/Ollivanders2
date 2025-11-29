package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import org.bukkit.entity.Player;

/**
 * Test suite for the ANIMAGUS_INCANTATION effect.
 *
 * <p>Tests the incantation marker effect that temporarily flags a player as actively reciting
 * the Animagus spell. Verifies that the effect cannot be set to permanent status and that
 * it properly functions as a passive state marker without active tick processing or event handling.</p>
 */
public class AnimagusIncantationTest extends NotPermanentEffectTestSuper {
    /**
     * Create an ANIMAGUS_INCANTATION effect for testing.
     *
     * <p>Instantiates a new ANIMAGUS_INCANTATION effect with the specified parameters. This method
     * is called by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new ANIMAGUS_INCANTATION effect targeting the specified player
     */
    ANIMAGUS_INCANTATION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new ANIMAGUS_INCANTATION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic ANIMAGUS_INCANTATION effect behavior and aging.
     *
     * <p>Validates that the ANIMAGUS_INCANTATION effect properly ages over time and is automatically killed
     * when its duration expires. Uses the common aging helper to verify standard effect lifecycle.</p>
     */
    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    /**
     * Run all event handler tests for the ANIMAGUS_INCANTATION effect.
     *
     * <p>ANIMAGUS_INCANTATION has no event handlers to test.</p>
     */
    void eventHandlerTests() {}

    /**
     * Test ANIMAGUS_INCANTATION effect cleanup.
     *
     * <p>doRemove for ANIMAGUS_INCANTATION doesn't do anything.</p>
     */
    void doRemoveTest() {}
}
