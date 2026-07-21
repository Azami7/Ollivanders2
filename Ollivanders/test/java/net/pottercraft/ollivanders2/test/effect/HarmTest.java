package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HARM;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link HARM}.
 *
 * @author Azami7
 * @see PotionEffectTest
 */
public class HarmTest extends PotionEffectTest {
    @Override
    HARM createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HARM(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
