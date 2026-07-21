package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SPEED_SPEEDIEST;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SPEED_SPEEDIEST}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class SpeedSpeediestTest extends PotionEffectTest {
    @Override
    SPEED_SPEEDIEST createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SPEED_SPEEDIEST(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
