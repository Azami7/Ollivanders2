package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LYCANTHROPY_SPEECH;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LYCANTHROPY_SPEECH}, which babbles like BABBLING but is always permanent. Inherits the babbling
 * coverage from BabblingTest and overrides {@link #isPermanentTest()} to assert the permanent flag cannot be cleared.
 *
 * @see BabblingTest
 */
public class LycanthropySpeechTest extends BabblingTest {
    @Override
    LYCANTHROPY_SPEECH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LYCANTHROPY_SPEECH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {}

    /**
     * LYCANTHROPY_SPEECH is created permanent and stays permanent after setPermanent(false).
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created;");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
