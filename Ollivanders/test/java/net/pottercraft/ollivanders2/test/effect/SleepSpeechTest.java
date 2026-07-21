package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.SLEEP_SPEECH;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SLEEP_SPEECH}. Inherits the chat-replacement coverage from {@link BabblingTest} and adds
 * checks for its always-permanent behavior.
 *
 * @see BabblingTest
 */
public class SleepSpeechTest extends BabblingTest {
    @Override
    SLEEP_SPEECH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SLEEP_SPEECH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * checkEffect() does nothing in SLEEP_SPEECH
     */
    @Override
    void checkEffectTest() {}

    /**
     * SLEEP_SPEECH always permanent
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
