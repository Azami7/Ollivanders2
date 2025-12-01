package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FUMOS;
import org.bukkit.entity.Player;

/**
 * Test suite for the FUMOS effect.
 *
 * <p>FUMOS is a smoke shield spell that displays continuous campfire smoke particle effects
 * around the player while blocking incoming projectile spells. This test validates basic effect
 * creation, duration management, spell blocking, and lifecycle behavior.</p>
 */
public class FumosTest extends ShieldSpellEffectTestSuper {
    /**
     * Create a FUMOS effect for testing.
     *
     * <p>Instantiates a new FUMOS effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new FUMOS effect targeting the specified player
     */
    @Override
    FUMOS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FUMOS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test FUMOS effect cleanup.
     *
     * <p>FUMOS has no persistent state to clean up when removed.</p>
     */
    @Override
    void doRemoveTest() {
    }
}
