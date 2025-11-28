package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.UNLUCK;

/**
 * Test suite for the UNLUCK potion effect.
 *
 * <p>UnluckTest validates that the UNLUCK effect correctly applies Minecraft's native
 * unlock potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on UNLUCK-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The UNLUCK potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (UNLUCK)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the UNLUCK class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see UNLUCK for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class UnluckTest extends PotionEffectSuperTest {
    /**
     * Create a UNLUCK effect for testing.
     *
     * <p>Instantiates a new UNLUCK effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - UNLUCK effects cannot be permanent
     * @return a new UNLUCK effect targeting the test player
     */
    UNLUCK createEffect(int durationInTicks, boolean isPermanent) {
        return new UNLUCK(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
