package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import net.pottercraft.ollivanders2.effect.URSIFORS_MAXIMA;
import org.bukkit.entity.Player;

/**
 * Test suite for the URSIFORS_MAXIMA effect (polar bear / panda transformation).
 *
 * @see URSIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class UrsiforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new URSIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
