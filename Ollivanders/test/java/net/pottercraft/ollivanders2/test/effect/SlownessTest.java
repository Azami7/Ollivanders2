package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SLOWNESS;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SLOWNESS}.
 *
 * @see PotionEffectTest
 */
public class SlownessTest extends PotionEffectTest {
    /**
     * {@inheritDoc}
     */
    @Override
    SLOWNESS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SLOWNESS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
