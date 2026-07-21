package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FLAGRANTE_BURNING;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link FLAGRANTE_BURNING}.
 *
 * @see BurningTest
 */
public class FlagranteBurningTest extends BurningTest {
    @Override
    FLAGRANTE_BURNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FLAGRANTE_BURNING(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * FLAGRANTE_BURNING is always permanent, and setPermanent(false) cannot change that.
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
