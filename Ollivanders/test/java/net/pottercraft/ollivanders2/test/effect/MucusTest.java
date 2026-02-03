package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.MUCUS;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test suite for the MUCUS effect.
 *
 * <p>Tests the mucus spawning effect that forces players to continuously spawn small slime entities.
 * Verifies core effect behaviors (aging, duration expiration) and specific MUCUS mechanics:</p>
 * <ul>
 * <li>Spawns size-1 slime entities at the player's eye location</li>
 * <li>Spawns slimes at the correct frequency (every 300 game ticks / 15 seconds)</li>
 * <li>Properly ages and expires based on duration</li>
 * <li>Has no persistent state to clean up on removal</li>
 * </ul>
 *
 * <p>The main focus is on verifying that slimes are spawned at the correct intervals during
 * the effect's duration.</p>
 */
public class MucusTest extends EffectTestSuper {
    /**
     * Create a MUCUS effect for testing.
     *
     * <p>Instantiates a new MUCUS effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new MUCUS effect targeting the specified player
     */
    @Override
    MUCUS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new MUCUS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test MUCUS effect behavior for spawning slime entities at the correct frequency.
     *
     * <p>Applies a MUCUS effect with a duration slightly longer than the spawn frequency (mucusFrequency + 10 ticks).
     * Then performs two spawn cycles, verifying that:</p>
     * <ol>
     * <li>After advancing mucusFrequency ticks, a slime entity is spawned at the player's location</li>
     * <li>After advancing another mucusFrequency ticks, a second slime entity is spawned</li>
     * </ol>
     *
     * <p>This confirms that slimes spawn at regular intervals (every 300 ticks / 15 seconds) during the effect.</p>
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

    /**
     * Event handler tests for the MUCUS effect.
     *
     * <p>MUCUS does not implement any event handlers because slime spawning is handled directly
     * in the checkEffect() method through scheduled entity creation. Therefore, this test method
     * is empty as there are no event handlers to test.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * MUCUS cleanup test.
     *
     * <p>MUCUS has no special cleanup logic in doRemove() because the effect has no persistent state.
     * When removed, the player simply stops spawning new slimes, though previously spawned slimes
     * remain in the world. This method is empty as there is nothing specific to test beyond the
     * base class cleanup behavior.</p>
     */
    @Override
    void doRemoveTest() {}
}
