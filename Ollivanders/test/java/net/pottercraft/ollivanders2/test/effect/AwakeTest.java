package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.AWAKE;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the AWAKE effect.
 *
 * <p>Tests the insomnia effect that prevents players from sleeping by canceling bed enter events.
 * Verifies that the effect ages properly each tick, is killed when duration expires, and correctly
 * prevents players from entering beds while active.</p>
 */
public class AwakeTest extends EffectTestSuper {
    /**
     * Create an AWAKE effect for testing.
     *
     * <p>Instantiates a new AWAKE effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new AWAKE effect targeting the specified player
     */
    AWAKE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new AWAKE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic AWAKE effect behavior and aging.
     *
     * <p>Validates that the AWAKE effect properly ages over time and is automatically killed
     * when its duration expires. Uses the common aging helper to verify standard effect lifecycle.</p>
     */
    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    /**
     * Run all event handler tests for the AWAKE effect.
     *
     * <p>Executes the bed enter event test to verify that the effect correctly intercepts
     * and cancels attempts by affected players to enter beds.</p>
     */
    void eventHandlerTests() {
        doOnPlayerBedEnterEventTest();
    }

    /**
     * Test that AWAKE effect prevents players from entering beds.
     *
     * <p>Creates a bed block in the test world, places a player at the bed location, applies
     * the AWAKE effect, then simulates a PlayerBedEnterEvent. Verifies that the event is
     * cancelled by the AWAKE effect, preventing the player from sleeping.</p>
     */
    void doOnPlayerBedEnterEventTest() {
        Block bed = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        bed.setType(Material.BLUE_BED);

        PlayerMock target = mockServer.addPlayer();

        target.setLocation(new Location(testWorld, 200, 4, 300));

        AWAKE awake = (AWAKE)addEffect(target, 100, false);

        // call a player bed enter event
        PlayerBedEnterEvent event = new PlayerBedEnterEvent(target, bed, PlayerBedEnterEvent.BedEnterResult.OK);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerBedEnterEvent was not canceled by AWAKE");
    }

    /**
     * doRemove() for AWAKE does not do anything
     */
    void doRemoveTest() {}
}
