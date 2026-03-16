package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BOVIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Test suite for the BOVIFORS_MAXIMA effect (cow transformation).
 *
 * @see BOVIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class BoviforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BOVIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
