package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LAPIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Test suite for the LAPIFORS_MAXIMA effect (rabbit transformation).
 *
 * @see LAPIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class LapiforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LAPIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
