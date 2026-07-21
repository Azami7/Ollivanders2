package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link HIGHER_SKILL}.
 *
 * @see EffectTestSuper
 */
public class HigherSkillTest extends EffectTestSuper {
    @Override
    HIGHER_SKILL createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HIGHER_SKILL(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {
    }

    @Override
    void eventHandlerTests() {}

    @Override
    void doRemoveTest() {}
}
