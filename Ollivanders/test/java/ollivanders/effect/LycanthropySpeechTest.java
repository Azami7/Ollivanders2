package ollivanders.effect;

import net.pottercraft.ollivanders2.effect.LYCANTHROPY_SPEECH;
import net.pottercraft.ollivanders2.effect.O2Effect;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the LYCANTHROPY_SPEECH effect.
 *
 * <p>LYCANTHROPY_SPEECH is a permanent effect applied during lycanthropy transformations that causes
 * players to speak nonsense words, similar to BABBLING. Inherits all chat message replacement tests
 * from BabblingTest to verify the babbling behavior works correctly.</p>
 *
 * <p>Special Behavior: LYCANTHROPY_SPEECH is always permanent and cannot be made temporary via setPermanent(false).
 * This test overrides isPermanentTest() to verify that the permanent flag cannot be changed.</p>
 *
 * <p>Note: checkEffectTest() is overridden to be empty because LYCANTHROPY_SPEECH has no special aging behavior
 * beyond the base O2Effect class.</p>
 */
public class LycanthropySpeechTest extends BabblingTest {
    LYCANTHROPY_SPEECH createEffect(int durationInTicks, boolean isPermanent) {
        return new LYCANTHROPY_SPEECH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * checkEffect() does nothing in LYCANTHROPY_SPEECH
     */
    void checkEffectTest() {}

    /**
     * LYCANTHROPY_SPEECH always permanent
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created;");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
