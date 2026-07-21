package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HARM;
import net.pottercraft.ollivanders2.effect.HARM_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link HARM_ANTIDOTE_LESSER}, the partial-strength antidote to the {@link HARM} effect.
 *
 * @see PotionEffectAntidoteTest
 */
public class HarmAntidoteLesserTest extends PotionEffectAntidoteTest {
    /**
     * {@inheritDoc}
     */
    @Override
    HARM_ANTIDOTE_LESSER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HARM_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    O2Effect addEffectToTarget(Player target, int duration) {
        return new HARM(testPlugin, duration, false, target.getUniqueId());
    }
}