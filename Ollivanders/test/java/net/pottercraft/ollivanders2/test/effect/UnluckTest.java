package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.UNLUCK;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link UNLUCK}, which applies Minecraft's native unluck potion effect.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class UnluckTest extends PotionEffectTest {
    /**
     * {@inheritDoc}
     */
    @Override
    UNLUCK createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new UNLUCK(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
