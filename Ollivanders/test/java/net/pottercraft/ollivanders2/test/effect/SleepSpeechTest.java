package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.SLEEP_SPEECH;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the SLEEP_SPEECH effect.
 *
 * <p>SLEEP_SPEECH is a permanent effect that causes players to speak nonsense words while sleeping,
 * similar to BABBLING. Inherits all chat message replacement tests from BabblingTest to verify
 * the babbling behavior works correctly.</p>
 *
 * <p>Special Behavior: SLEEP_SPEECH is always permanent and cannot be made temporary via setPermanent(false).
 * This test overrides isPermanentTest() to verify that the permanent flag cannot be changed.</p>
 *
 * <p>Note: checkEffectTest() is overridden to be empty because SLEEP_SPEECH has no special aging behavior
 * beyond the base O2Effect class.</p>
 */
public class SleepSpeechTest extends BabblingTest {
    /**
     * Create a SLEEP_SPEECH effect for testing.
     *
     * <p>Creates a permanent SLEEP_SPEECH effect. The isPermanent parameter is ignored as
     * SLEEP_SPEECH effects are always permanent by design.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration parameter (unused for permanent effects)
     * @param isPermanent     ignored; SLEEP_SPEECH effects are always permanent
     * @return the newly created SLEEP_SPEECH effect instance
     */
    SLEEP_SPEECH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SLEEP_SPEECH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * checkEffect() does nothing in SLEEP_SPEECH
     */
    void checkEffectTest() {}

    /**
     * SLEEP_SPEECH always permanent
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created;");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
