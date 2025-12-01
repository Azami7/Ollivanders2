package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FUMOS_DUO;
import org.bukkit.entity.Player;

/**
 * Test suite for the FUMOS_DUO effect.
 *
 * <p>FUMOS_DUO is an enhanced variant of the FUMOS smoke shield spell. Like FUMOS, it displays
 * continuous campfire smoke particle effects around the player while blocking incoming projectile
 * spells. This test validates basic effect creation, duration management, spell blocking, and lifecycle behavior.</p>
 */
public class FumosDuoTest extends ShieldSpellEffectTestSuper {
    /**
     * Create a FUMOS_DUO effect for testing.
     *
     * <p>Instantiates a new FUMOS_DUO effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new FUMOS_DUO effect targeting the specified player
     */
    @Override
    FUMOS_DUO createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FUMOS_DUO(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test FUMOS_DUO effect cleanup.
     *
     * <p>FUMOS_DUO has no persistent state to clean up when removed.</p>
     */
    @Override
    void doRemoveTest() {
    }
}
