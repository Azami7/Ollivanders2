package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.AGNIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Test suite for the AGNIFORS_MAXIMA effect (sheep transformation).
 *
 * @see AGNIFORS_MAXIMA
 * @see PlayerTransformBaseTest
 */
public class AgniforsMaximaTest extends PlayerTransformBaseTest {
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new AGNIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
