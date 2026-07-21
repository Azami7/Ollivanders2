package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LAPIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link LAPIFORS_MAXIMA} (rabbit transformation).
 *
 * @see PlayerTransformBaseTest
 */
public class LapiforsMaximaTest extends PlayerTransformBaseTest {
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LAPIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
