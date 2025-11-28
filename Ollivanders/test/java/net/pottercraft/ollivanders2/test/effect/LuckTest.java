package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LUCK;

/**
 * Test suite for the LUCK potion effect.
 *
 * <p>LuckTest validates that the LUCK effect correctly applies Minecraft's native
 * luck potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on LUCK-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The LUCK potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (LUCK)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the LUCK class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see LUCK for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class LuckTest extends PotionEffectSuperTest {
    /**
     * Create a LUCK effect for testing.
     *
     * <p>Instantiates a new LUCK effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - LUCK effects cannot be permanent
     * @return a new LUCK effect targeting the test player
     */
    LUCK createEffect(int durationInTicks, boolean isPermanent) {
        return new LUCK(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
