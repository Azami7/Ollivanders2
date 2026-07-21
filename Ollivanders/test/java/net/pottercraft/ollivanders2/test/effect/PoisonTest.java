package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.POISON;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link POISON}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class PoisonTest extends PotionEffectTest {
    @Override
    POISON createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new POISON(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
