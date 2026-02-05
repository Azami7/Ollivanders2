package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.STRENGTH;
import org.bukkit.entity.Player;

/**
 * Test suite for the STRENGTH effect.
 *
 * <p>Verifies that the STRENGTH effect functions correctly when applied to players.
 * Tests the effect's properties, duration handling, and permanent effect support using the
 * base test infrastructure.</p>
 *
 * @see PotionEffectSuperTest for the base test infrastructure
 */
public class StrengthTest extends PotionEffectSuperTest {
    /**
     * Create a STRENGTH effect instance for testing.
     *
     * <p>Instantiates a new STRENGTH effect with the specified duration and permanent flag.
     * This method is called by the inherited test framework to create effect instances for
     * testing various duration scenarios and permanent effect handling.</p>
     *
     * @param target the player who will be affected by the effect
     * @param durationInTicks the duration of the effect in server ticks
     * @param isPermanent whether the effect should be permanent
     * @return a new STRENGTH effect instance
     */
    @Override
    STRENGTH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new STRENGTH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
