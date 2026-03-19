package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FELIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Test suite for the FELIFORS_MAXIMA effect (cat transformation).
 *
 * @see FELIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class FeliforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FELIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
