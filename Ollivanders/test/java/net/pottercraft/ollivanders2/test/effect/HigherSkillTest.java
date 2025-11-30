package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import org.bukkit.entity.Player;

/**
 * Test suite for the HIGHER_SKILL effect.
 *
 * <p>HIGHER_SKILL is a passive marker effect that boosts the player's effective skill level.
 * This test validates basic effect creation, duration management, and lifecycle behavior.</p>
 */
public class HigherSkillTest extends EffectTestSuper {
    /**
     * Create a HIGHER_SKILL effect for testing.
     *
     * <p>Instantiates a new HIGHER_SKILL effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new HIGHER_SKILL effect targeting the specified player
     */
    @Override
    HIGHER_SKILL createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HIGHER_SKILL(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic HIGHER_SKILL effect behavior.
     *
     * <p>HIGHER_SKILL is a passive marker effect with no active behavior. This test validates
     * that the effect properly ages and expires without side effects.</p>
     */
    @Override
    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    /**
     * Run all event handler tests for the HIGHER_SKILL effect.
     *
     * <p>HIGHER_SKILL has no event handlers to test.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Test HIGHER_SKILL effect cleanup.
     *
     * <p>HIGHER_SKILL has no persistent state to clean up.</p>
     */
    @Override
    void doRemoveTest() {}
}
