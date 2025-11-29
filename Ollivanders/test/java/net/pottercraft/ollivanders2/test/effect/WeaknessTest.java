package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.WEAKNESS;
import org.bukkit.entity.Player;

/**
 * Test suite for the WEAKNESS potion effect.
 *
 * <p>WeaknessTest validates that the WEAKNESS effect correctly applies Minecraft's native
 * weakness potion effect to target players. This test class extends {@link PotionEffectSuperTest}
 * to inherit comprehensive testing of potion effect behavior while focusing on WEAKNESS-specific
 * implementation details.</p>
 *
 * <p>Test Coverage:</p>
 * <p>BlindnessTest inherits all test methods from PotionEffectSuperTest, which comprehensively
 * validates:
 * <ul>
 * <li>The WEAKNESS potion effect is applied to the target player</li>
 * <li>The potion effect has the correct type (WEAKNESS)</li>
 * <li>The potion effect has the correct duration (clamped to 2400-6000 ticks)</li>
 * <li>The potion effect has the correct strength/amplifier (set by the WEAKNESS class)</li>
 * <li>Duration bounds are enforced (minimum 2400 ticks, maximum 6000 ticks)</li>
 * <li>The effect cannot be marked as permanent</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see WEAKNESS for the effect implementation being tested
 * @see PotionEffectSuperTest for the comprehensive potion effect testing framework
 */
public class WeaknessTest extends PotionEffectSuperTest {
    /**
     * Create a WEAKNESS effect for testing.
     *
     * <p>Instantiates a new WEAKNESS effect with the specified parameters. This method is called
     * by the inherited test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks, will be clamped to 2400-6000 ticks
     * @param isPermanent     ignored - WEAKNESS effects cannot be permanent
     * @return a new WEAKNESS effect targeting the test player
     */
    WEAKNESS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new WEAKNESS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
