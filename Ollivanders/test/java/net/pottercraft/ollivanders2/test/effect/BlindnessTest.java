package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BLINDNESS;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link BLINDNESS}.
 *
 * @see PotionEffectTest
 */
public class BlindnessTest extends PotionEffectTest {
    /**
     * {@inheritDoc}
     */
    @Override
    BLINDNESS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BLINDNESS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
