package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.UNLUCK;
import net.pottercraft.ollivanders2.effect.UNLUCK_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link UNLUCK_ANTIDOTE_LESSER}.
 *
 * @author Azami7
 * @see PotionEffectAntidoteTest
 */
public class UnluckAntidoteLesserTest extends PotionEffectAntidoteTest {
    @Override
    UNLUCK_ANTIDOTE_LESSER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new UNLUCK_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * The countered effect is {@link UNLUCK}.
     */
    @Override
    O2Effect addEffectToTarget(Player target, int duration) {
        return new UNLUCK(testPlugin, duration, false, target.getUniqueId());
    }
}