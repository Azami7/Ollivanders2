package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SLEEPING;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the AWAKE effect.
 *
 * <p>Tests the insomnia effect that prevents players from sleeping. Verifies core effect behaviors
 * (aging, duration expiration) and specific AWAKE mechanics:</p>
 * <ul>
 * <li>Removes active SLEEPING effects when applied (effect override)</li>
 * <li>Cancels PlayerBedEnterEvent to prevent new sleep attempts</li>
 * <li>Properly ages and expires based on duration</li>
 * </ul>
 *
 * <p>Special attention is given to the interaction between AWAKE and SLEEPING effects to ensure
 * they are mutually exclusive and properly resolve conflicts.</p>
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
    @Override
    AWAKE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new AWAKE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test AWAKE effect behavior for waking sleeping players and preventing bed entry.
     *
     * <p>Performs two comprehensive tests:</p>
     * <ol>
     * <li><strong>Test 1: AWAKE removes SLEEPING effect</strong> - Applies a SLEEPING effect to a player,
     * advances ticks to initialize it, then applies AWAKE and advances ticks again. Verifies that both effects
     * are active after initialization, then confirms that SLEEPING is removed after AWAKE processes.</li>
     * <li><strong>Test 2: AWAKE prevents bed entry</strong> - Creates a bed, applies AWAKE to a player,
     * simulates a PlayerBedEnterEvent, and verifies the event is cancelled.</li>
     * </ol>
     *
     * <p>The timing sequence is critical: SLEEPING must initialize before AWAKE is applied to ensure
     * proper effect state for testing the interaction between the two effects.</p>
     */
    @Override
    void checkEffectTest() {
        Ollivanders2.debug = true;

        // Test 1: AWAKE wakes up a sleeping player and removes SLEEPING effect
        PlayerMock target = mockServer.addPlayer();

        // Apply SLEEPING effect first
        SLEEPING sleeping = new SLEEPING(testPlugin, 100, false, target.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(sleeping);
        // Advance ticks so SLEEPING initializes and puts player to sleep
        mockServer.getScheduler().performTicks(5);

        // Verify SLEEPING effect is active
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.SLEEPING),
            "SLEEPING effect was not added to target");

        // Verify player is sleeping (should be set by SLEEPING.playerSleep())
        // commented out because MockBukkit has not yet implemented player sleeping
        //assertTrue(target.isSleeping(), "Player was not put to sleep by SLEEPING effect");

        // Apply AWAKE effect
        addEffect(target, 100, false);

        // Advance ticks so AWAKE's checkEffect() runs and wakes the player
        mockServer.getScheduler().performTicks(5);

        // verify AWAKE effect is active
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.AWAKE),
                "AWAKE effect was not added to target");

        // Verify player was woken up
        // commented out because MockBukkit has not yet implemented player sleeping
        // assertFalse(target.isSleeping(), "AWAKE did not wake up sleeping player");

        // Verify SLEEPING effect was removed
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.SLEEPING),
            "SLEEPING effect was not removed by AWAKE");

        // Test 2: AWAKE prevents player from entering a bed
        PlayerMock target2 = mockServer.addPlayer();
        Block bed = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        bed.setType(Material.BLUE_BED);
        target2.setLocation(new Location(testWorld, 200, 4, 300));

        // Apply AWAKE effect
        addEffect(target2, 100, false);

        // Try to trigger a bed enter event
        PlayerBedEnterEvent event = new PlayerBedEnterEvent(target2, bed, PlayerBedEnterEvent.BedEnterResult.OK);
        mockServer.getPluginManager().callEvent(event);

        // Verify the event was cancelled
        assertTrue(event.isCancelled(), "AWAKE did not prevent player from entering bed");
    }

    /**
     * Run all event handler tests for the AWAKE effect.
     *
     * <p>Executes the bed enter event test to verify that the effect correctly intercepts
     * and cancels attempts by affected players to enter beds.</p>
     */
    @Override
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
    @Override
    void doRemoveTest() {}
}
