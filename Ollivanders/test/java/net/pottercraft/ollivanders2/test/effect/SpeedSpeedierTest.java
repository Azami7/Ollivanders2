package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SPEED_SPEEDIER;
import org.bukkit.entity.Player;

/**
 * Test suite for the SPEED_SPEEDIER potion effect.
 *
 * <p>SpeedSpeedierTest validates that the SPEED_SPEEDIER effect correctly applies Minecraft's native
 * speed potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on SPEED_SPEEDIER-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The SPEED_SPEEDIER potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (SPEED_SPEEDIER)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the SPEED_SPEEDIER class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see SPEED_SPEEDIER for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class SpeedSpeedierTest extends PotionEffectSuperTest {
    /**
     * Create a SPEED_SPEEDIER effect for testing.
     *
     * <p>Instantiates a new SPEED_SPEEDIER effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - SPEED_SPEEDIER effects cannot be permanent
     * @return a new SPEED_SPEEDIER effect targeting the test player
     */
    @Override
    SPEED_SPEEDIER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SPEED_SPEEDIER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
