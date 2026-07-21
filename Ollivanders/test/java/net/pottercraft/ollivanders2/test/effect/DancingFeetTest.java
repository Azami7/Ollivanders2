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
 * Unit tests for {@link DANCING_FEET}.
 *
 * @see EffectTestSuper
 */
public class DancingFeetTest extends EffectTestSuper {
    @Override
    DANCING_FEET createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new DANCING_FEET(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * The player sneaks immediately, then stops sneaking and changes facing once the dance cooldown elapses.
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

    @Override
    void eventHandlerTests() {
        // PlayerToggleSneakEvent not yet implemented in MockBukkit and including this causes the whole test to be skipped
        //doOnPlayerToggleSneakEventTest();
        doOnPlayerToggleSprintEventTest();
        doOnPlayerMoveEventTest();
    }

    /**
     * A player affected by DANCING_FEET cannot manually toggle sneak.
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
     * A player affected by DANCING_FEET cannot activate sprint.
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
     * A player affected by DANCING_FEET cannot manually move.
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
