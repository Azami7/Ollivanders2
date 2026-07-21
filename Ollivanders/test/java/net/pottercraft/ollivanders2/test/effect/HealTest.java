package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HEAL;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link HEAL}, which applies Minecraft's native heal potion effect.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class HealTest extends PotionEffectTest {
    /**
     * {@inheritDoc}
     */
    @Override
    HEAL createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HEAL(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
