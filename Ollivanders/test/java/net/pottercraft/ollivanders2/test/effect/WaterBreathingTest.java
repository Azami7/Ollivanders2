package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.WATER_BREATHING;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link WATER_BREATHING}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class WaterBreathingTest extends PotionEffectTest {
    @Override
    WATER_BREATHING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new WATER_BREATHING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
