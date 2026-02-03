package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SPEED_SPEEDIEST;
import org.bukkit.entity.Player;

/**
 * Test suite for the SPEED_SPEEDIEST potion effect.
 *
 * <p>SpeedSpeediestTest validates that the SPEED_SPEEDIEST effect correctly applies Minecraft's native
 * speed potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on SPEED_SPEEDIEST-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The SPEED_SPEEDIEST potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (SPEED_SPEEDIEST)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the SPEED_SPEEDIEST class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see SPEED_SPEEDIEST for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class SpeedSpeediestTest extends PotionEffectSuperTest {
    /**
     * Create a SPEED_SPEEDIEST effect for testing.
     *
     * <p>Instantiates a new SPEED_SPEEDIEST effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - SPEED_SPEEDIEST effects cannot be permanent
     * @return a new SPEED_SPEEDIEST effect targeting the test player
     */
    @Override
    SPEED_SPEEDIEST createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SPEED_SPEEDIEST(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
