package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.BURNING;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the BURNING effect.
 *
 * <p>BURNING is an effect that sets players on fire, dealing damage over time. This test
 * validates basic effect creation, duration management, and lifecycle behavior.</p>
 */
public class BurningTest extends EffectTestSuper {
    /**
     * Create a BURNING effect for testing.
     *
     * <p>Instantiates a new BURNING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new BURNING effect targeting the specified player
     */
    @Override
    BURNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BURNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test BURNING effect behavior.
     *
     * <p>Validates that the BURNING effect correctly applies damage over time.
     * Verifies the target's health decreases after the effect duration expires.</p>
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();

        // verify burning does damage
        BURNING burning = createEffect(target, 100, false);
        double targetHealth = target.getHealth();

        Ollivanders2API.getPlayers().playerEffects.addEffect(burning);

        mockServer.getScheduler().performTicks(burning.getRemainingDuration());

        assertTrue(targetHealth > target.getHealth(), "Target not damaged by burning effect");
    }

    /**
     * Run all event handler tests for the BURNING effect.
     *
     * <p>BURNING has no event handlers to test.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Test BURNING effect cleanup.
     *
     * <p>doRemove for BURNING doesn't do anything.</p>
     */
    @Override
    void doRemoveTest() {}
}
