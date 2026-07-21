package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.BURNING;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BURNING}.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
public class BurningTest extends EffectTestSuper {
    @Override
    BURNING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BURNING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Burning damages the target over its duration.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();

        BURNING burning = createEffect(target, 100, false);
        double targetHealth = target.getHealth();

        Ollivanders2API.getPlayers().playerEffects.addEffect(burning);

        mockServer.getScheduler().performTicks(burning.getRemainingDuration());

        assertTrue(targetHealth > target.getHealth(), "Target not damaged by burning effect");
    }

    @Override
    void eventHandlerTests() {}

    @Override
    void doRemoveTest() {}
}
