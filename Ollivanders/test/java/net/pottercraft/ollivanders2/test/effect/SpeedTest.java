package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SPEED;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SPEED}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class SpeedTest extends PotionEffectTest {
    @Override
    SPEED createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SPEED(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
