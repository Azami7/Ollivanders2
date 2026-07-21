package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.REGENERATION;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link REGENERATION}.
 *
 * @see PotionEffectTest
 */
public class RegenerationTest extends PotionEffectTest {
    /**
     * {@inheritDoc}
     */
    @Override
    REGENERATION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new REGENERATION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
