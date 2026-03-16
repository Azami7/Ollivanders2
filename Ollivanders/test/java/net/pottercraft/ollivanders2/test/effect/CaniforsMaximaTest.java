package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.CANIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Test suite for the CANIFORS_MAXIMA effect (dog transformation).
 *
 * @see CANIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class CaniforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new CANIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
