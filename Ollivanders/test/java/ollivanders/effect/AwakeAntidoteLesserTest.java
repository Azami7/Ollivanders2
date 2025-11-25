package ollivanders.effect;

import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.AWAKE_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;

/**
 * Test suite for the AWAKE_ANTIDOTE_LESSER effect.
 *
 * <p>Tests the antidote effect that counteracts the AWAKE insomnia effect.
 * Verifies that the antidote correctly reduces or removes AWAKE effects
 * based on its partial strength, and does not affect unrelated effects.</p>
 */
public class AwakeAntidoteLesserTest extends O2EffectAntidoteSuperTest {
    AWAKE_ANTIDOTE_LESSER createEffect(int durationInTicks, boolean isPermanent) {
        return new AWAKE_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    O2Effect addEffectToTarget(int duration) {
        return new AWAKE(testPlugin, duration, false, target.getUniqueId());
    }
}
