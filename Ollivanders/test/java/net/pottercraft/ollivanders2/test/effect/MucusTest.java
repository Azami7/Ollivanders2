package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.MUCUS;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link MUCUS}.
 *
 * @see EffectTestSuper
 */
public class MucusTest extends EffectTestSuper {
    @Override
    MUCUS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new MUCUS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * MUCUS spawns a slime at the player every {@link MUCUS#mucusFrequency} ticks.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);
        MUCUS effect = (MUCUS) addEffect(target, MUCUS.mucusFrequency + 10, false);

        // every MUCUS.mucusFrequency a slime is spawned at the target's location
        mockServer.getScheduler().performTicks(MUCUS.mucusFrequency);
        Entity slime = null;
        for (Entity entity: testWorld.getEntities()) {
            if (entity.getUniqueId().equals(target.getUniqueId()))
                continue;

            if (entity.getType() == EntityType.SLIME)
                slime = entity;
        }
        assertNotNull(slime, "Slime entity not found in world");

        mockServer.getScheduler().performTicks(MUCUS.mucusFrequency);
        Entity slime2 = null;
        for (Entity entity: testWorld.getEntities()) {
            if (entity.getUniqueId().equals(target.getUniqueId()) || entity.getUniqueId().equals(slime.getUniqueId()))
                continue;

            if (entity.getType() == EntityType.SLIME)
                slime2 = entity;
        }
        assertNotNull(slime2, "Second slime entity not found in world");
    }

    @Override
    void eventHandlerTests() {}

    @Override
    void doRemoveTest() {}
}
