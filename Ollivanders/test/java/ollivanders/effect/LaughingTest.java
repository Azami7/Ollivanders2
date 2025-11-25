package ollivanders.effect;

import net.pottercraft.ollivanders2.effect.LAUGHING;

/**
 * Test suite for the LAUGHING effect.
 *
 * <p>LAUGHING is an effect that causes players to speak nonsense words, similar to BABBLING.
 * Inherits all tests from BabblingTest, including chat message replacement verification and
 * default effect aging behavior.</p>
 *
 * <p>This effect uses all default behavior from the BABBLING test suite without any special
 * overrides, as LAUGHING follows the standard effect patterns for probabilistic message modification.</p>
 */
public class LaughingTest extends BabblingTest {
    LAUGHING createEffect(int durationInTicks, boolean isPermanent) {
        return new LAUGHING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
