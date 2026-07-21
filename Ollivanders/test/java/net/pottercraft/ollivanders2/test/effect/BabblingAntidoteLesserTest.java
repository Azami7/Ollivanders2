package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.effect.BABBLING_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link BABBLING_ANTIDOTE_LESSER}.
 *
 * @see O2EffectAntidoteTest
 */
public class BabblingAntidoteLesserTest extends O2EffectAntidoteTest {
    @Override
    BABBLING_ANTIDOTE_LESSER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BABBLING_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    O2Effect addEffectToTarget(Player target, int duration) {
        return new BABBLING(testPlugin, duration, false, target.getUniqueId());
    }
}
