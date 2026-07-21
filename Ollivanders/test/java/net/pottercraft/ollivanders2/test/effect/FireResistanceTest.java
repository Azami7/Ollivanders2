package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FIRE_RESISTANCE;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link FIRE_RESISTANCE}.
 *
 * @see PotionEffectTest
 */
public class FireResistanceTest extends PotionEffectTest {
    @Override
    FIRE_RESISTANCE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FIRE_RESISTANCE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
