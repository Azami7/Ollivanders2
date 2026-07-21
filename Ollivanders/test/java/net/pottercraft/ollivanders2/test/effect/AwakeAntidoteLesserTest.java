package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.AWAKE_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link AWAKE_ANTIDOTE_LESSER}, the antidote that counteracts the {@link AWAKE} insomnia effect.
 *
 * @see O2EffectAntidoteTest
 */
public class AwakeAntidoteLesserTest extends O2EffectAntidoteTest {
    @Override
    AWAKE_ANTIDOTE_LESSER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new AWAKE_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    O2Effect addEffectToTarget(Player target, int duration) {
        return new AWAKE(testPlugin, duration, false, target.getUniqueId());
    }
}
