package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.WEAKNESS;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link WEAKNESS}. Potion-effect behavior is covered by the inherited {@link PotionEffectTest} tests.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class WeaknessTest extends PotionEffectTest {
    @Override
    WEAKNESS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new WEAKNESS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
