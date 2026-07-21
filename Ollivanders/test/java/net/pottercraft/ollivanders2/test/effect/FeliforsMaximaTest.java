package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FELIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link FELIFORS_MAXIMA} (cat transformation).
 *
 * @see PlayerTransformBaseTest
 */
public class FeliforsMaximaTest extends PlayerTransformBaseTest {
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FELIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
