package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.WATER_BREATHING;
import org.bukkit.entity.Player;

/**
 * Test suite for the WATER_BREATHING potion effect.
 *
 * <p>WaterBreathingTest validates that the WATER_BREATHING effect correctly applies Minecraft's native
 * water breathing potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on WATER_BREATHING-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The WATER_BREATHING potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (WATER_BREATHING)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the WATER_BREATHING class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see WATER_BREATHING for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class WaterBreathingTest extends PotionEffectSuperTest {
    /**
     * Create a WATER_BREATHING effect for testing.
     *
     * <p>Instantiates a new WATER_BREATHING effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - WATER_BREATHING effects cannot be permanent
     * @return a new WATER_BREATHING effect targeting the test player
     */
    WATER_BREATHING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new WATER_BREATHING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
