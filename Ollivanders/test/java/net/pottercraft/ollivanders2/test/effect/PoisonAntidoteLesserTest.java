package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.POISON;
import net.pottercraft.ollivanders2.effect.POISON_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link POISON_ANTIDOTE_LESSER}, the partial-strength antidote to {@link POISON}.
 *
 * @see PotionEffectAntidoteTest
 */
public class PoisonAntidoteLesserTest extends PotionEffectAntidoteTest {
    @Override
    POISON_ANTIDOTE_LESSER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new POISON_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    O2Effect addEffectToTarget(Player target, int duration) {
        return new POISON(testPlugin, duration, false, target.getUniqueId());
    }
}