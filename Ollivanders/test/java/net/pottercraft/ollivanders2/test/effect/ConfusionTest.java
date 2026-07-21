package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.CONFUSION;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link CONFUSION}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class ConfusionTest extends PotionEffectTest {
    @Override
    CONFUSION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new CONFUSION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
