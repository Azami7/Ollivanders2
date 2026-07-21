package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.STRENGTH;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link STRENGTH}.
 *
 * @see PotionEffectTest
 */
public class StrengthTest extends PotionEffectTest {
    @Override
    STRENGTH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new STRENGTH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
