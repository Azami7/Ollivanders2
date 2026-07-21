package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.CANIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link CANIFORS_MAXIMA}.
 *
 * @see PlayerTransformBaseTest
 */
public class CaniforsMaximaTest extends PlayerTransformBaseTest {
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new CANIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
