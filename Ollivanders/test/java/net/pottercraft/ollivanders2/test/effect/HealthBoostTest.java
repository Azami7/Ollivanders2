package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HEALTH_BOOST;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link HEALTH_BOOST}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class HealthBoostTest extends PotionEffectTest {
    @Override
    HEALTH_BOOST createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HEALTH_BOOST(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
