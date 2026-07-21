package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.NIGHT_VISION;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link NIGHT_VISION}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class NightVisionTest extends PotionEffectTest {
    @Override
    NIGHT_VISION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new NIGHT_VISION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
