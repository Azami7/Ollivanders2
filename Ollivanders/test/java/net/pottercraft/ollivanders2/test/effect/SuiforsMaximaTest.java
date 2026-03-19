package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import net.pottercraft.ollivanders2.effect.SUIFORS_MAXIMA;
import org.bukkit.entity.Player;

/**
 * Test suite for the SUIFORS_MAXIMA effect (pig transformation).
 *
 * @see SUIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class SuiforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SUIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
