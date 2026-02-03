package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.NIGHT_VISION;
import org.bukkit.entity.Player;

/**
 * Test suite for the NIGHT_VISION potion effect.
 *
 * <p>NightVisionTest validates that the NIGHT_VISION effect correctly applies Minecraft's native
 * night vision potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on NIGHT_VISION-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The NIGHT_VISION potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (NIGHT_VISION)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the NIGHT_VISION class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see NIGHT_VISION for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class NightVisionTest extends PotionEffectSuperTest {
    /**
     * Create a NIGHT_VISION effect for testing.
     *
     * <p>Instantiates a new NIGHT_VISION effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - NIGHT_VISION effects cannot be permanent
     * @return a new NIGHT_VISION effect targeting the test player
     */
    @Override
    NIGHT_VISION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new NIGHT_VISION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
