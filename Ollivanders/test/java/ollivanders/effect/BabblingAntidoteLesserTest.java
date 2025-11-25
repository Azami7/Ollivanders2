package ollivanders.effect;

import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.effect.BABBLING_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;

/**
 * Test suite for the BABBLING_ANTIDOTE_LESSER effect.
 *
 * <p>Tests the antidote effect that counteracts the BABBLING silence effect.
 * Verifies that the antidote correctly reduces or removes BABBLING effects
 * based on its partial strength, and does not affect unrelated effects.</p>
 */
public class BabblingAntidoteLesserTest extends O2EffectAntidoteSuperTest {
    BABBLING_ANTIDOTE_LESSER createEffect(int durationInTicks, boolean isPermanent) {
        return new BABBLING_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    O2Effect addEffectToTarget(int duration) {
        return new BABBLING(testPlugin, duration, false, target.getUniqueId());
    }
}
