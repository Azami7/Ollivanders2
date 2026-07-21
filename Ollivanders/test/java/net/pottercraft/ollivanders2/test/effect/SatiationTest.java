package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SATIATION;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SATIATION}.
 *
 * @see PotionEffectTest
 */
public class SatiationTest extends PotionEffectTest {
    @Override
    SATIATION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SATIATION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
