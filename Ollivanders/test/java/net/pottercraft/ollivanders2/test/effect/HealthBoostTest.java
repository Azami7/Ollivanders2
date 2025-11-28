package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HEALTH_BOOST;

/**
 * Test suite for the HEALTH_BOOST potion effect.
 *
 * <p>HealthBoostTest validates that the HEALTH_BOOST effect correctly applies Minecraft's native
 * health boost potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on HEALTH_BOOST-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The HEALTH_BOOST potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (HEALTH_BOOST)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the HEALTH_BOOST class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see HEALTH_BOOST for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class HealthBoostTest extends PotionEffectSuperTest {
    /**
     * Create a HEALTH_BOOST effect for testing.
     *
     * <p>Instantiates a new HEALTH_BOOST effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - HEALTH_BOOST effects cannot be permanent
     * @return a new HEALTH_BOOST effect targeting the test player
     */
    HEALTH_BOOST createEffect(int durationInTicks, boolean isPermanent) {
        return new HEALTH_BOOST(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
