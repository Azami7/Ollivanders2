package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SPEED_SPEEDIER;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SPEED_SPEEDIER}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class SpeedSpeedierTest extends PotionEffectTest {
    @Override
    SPEED_SPEEDIER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SPEED_SPEEDIER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
