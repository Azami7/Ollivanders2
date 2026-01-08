package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.DANCING_FEET;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the DANCING_FEET effect.
 *
 * <p>DANCING_FEET is an effect that forces players to dance uncontrollably by repeatedly toggling their
 * sneak state and rotating them. This test validates effect creation, duration management, lifecycle behavior,
 * and event cancellation to prevent player control override.</p>
 */
public class DancingFeetTest extends EffectTestSuper {
    /**
     * Create a DANCING_FEET effect for testing.
     *
     * <p>Instantiates a new DANCING_FEET effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new DANCING_FEET effect targeting the specified player
     */
    @Override
    DANCING_FEET createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new DANCING_FEET(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic DANCING_FEET effect behavior.
     *
     * <p>Validates that the DANCING_FEET effect correctly applies its involuntary movement mechanics:
     * the sneak state toggles at the proper cooldown intervals, and the player's rotation changes
     * as they dance. Tests verify both the initial sneak toggle and the subsequent unsneaking after
     * the cooldown completes with a directional change.</p>
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        double targetYaw = target.getLocation().getYaw();
        target.setSneaking(false);

        // create and add the event to the manager
        DANCING_FEET dancingFeet = (DANCING_FEET) addEffect(target, Ollivanders2Common.ticksPerSecond * 10, false);

        // advance 5+ ticks to make sure the runnable executes
        mockServer.getScheduler().performTicks(10);

        // cooldown counter starts at zero so the target should be affected immediately
        // player is now sneaking
        assertTrue(target.isSneaking(), "Dancing feet did not make the player sneak");

        // advance the server 2 seconds + 5+ ticks to give the runnable time to execute
        mockServer.getScheduler().performTicks((Ollivanders2Common.ticksPerSecond * 2) + 10);
        // player is no longer sneaking
        assertFalse(target.isSneaking(), "Dancing feet did not make the player stop sneaking after cooldown");
        // player changed what direction they are facing
        assertNotEquals(targetYaw, target.getLocation().getYaw(), "Dancing feet did not change player's yaw");
    }

    /**
     * Run all event handler tests for the DANCING_FEET effect.
     *
     * <p>DANCING_FEET handles three player control events to prevent manual override. This test suite
     * validates that all three event types are properly cancelled when the effect is active.</p>
     */
    @Override
    void eventHandlerTests() {
        // PlayerToggleSneakEvent not yet implemented in MockBukkit and including this causes the whole test to be skipped
        //doOnPlayerToggleSneakEventTest();
        doOnPlayerToggleSprintEventTest();
        doOnPlayerMoveEventTest();
    }

    /**
     * Test that sneak toggle events are cancelled by the DANCING_FEET effect.
     *
     * <p>Validates that players affected by DANCING_FEET cannot manually toggle sneak, ensuring
     * the effect maintains complete control over the player's sneak state for dance movements.</p>
     */
    void doOnPlayerToggleSneakEventTest() {
        PlayerMock target = mockServer.addPlayer();
        addEffect(target, testWorld.getThunderDuration(), false);

        // create and call the event
        PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(target, true);
        mockServer.getPluginManager().callEvent(event);

        // advance the server
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerToggleSneakEvent not canceled by DANCING_FEET");
    }

    /**
     * Test that sprint toggle events are cancelled by the DANCING_FEET effect.
     *
     * <p>Validates that players affected by DANCING_FEET cannot activate sprint, which would
     * interfere with the controlled dance movement and teleportation cycles.</p>
     */
    void doOnPlayerToggleSprintEventTest() {
        PlayerMock target = mockServer.addPlayer();
        addEffect(target, testWorld.getThunderDuration(), false);

        // create and call the event
        PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(target, true);
        mockServer.getPluginManager().callEvent(event);

        // advance the server
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerToggleSprintEvent not canceled by DANCING_FEET");
    }

    /**
     * Test that movement events are cancelled by the DANCING_FEET effect.
     *
     * <p>Validates that players affected by DANCING_FEET cannot manually move, ensuring the effect
     * maintains complete control over player movement through involuntary teleportation.</p>
     */
    void doOnPlayerMoveEventTest() {
        PlayerMock target = mockServer.addPlayer();
        addEffect(target, testWorld.getThunderDuration(), false);

        // create and call the event
        Location currentLocation = target.getLocation();
        Location newLocation = new Location(currentLocation.getWorld(), currentLocation.getX() + 1, currentLocation.getY(), currentLocation.getZ());

        PlayerMoveEvent event = new PlayerMoveEvent(target, currentLocation, newLocation);
        mockServer.getPluginManager().callEvent(event);

        // advance the server
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerMoveEvent not canceled by DANCING_FEET");
    }

    /**
     * Dancing feet does not have any cleanup
     */
    @Override
    void doRemoveTest() {
    }
}
