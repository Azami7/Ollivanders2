package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HUNGER;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link HUNGER}. Potion-effect behavior is covered by the inherited {@link PotionEffectTest} tests.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class HungerTest extends PotionEffectTest {
    @Override
    HUNGER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HUNGER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
