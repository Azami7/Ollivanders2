package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LUCK;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link LUCK}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class LuckTest extends PotionEffectTest {
    @Override
    LUCK createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LUCK(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
