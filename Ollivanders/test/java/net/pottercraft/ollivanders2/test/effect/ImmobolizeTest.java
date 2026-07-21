package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link IMMOBILIZE}.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
public class ImmobolizeTest extends EffectTestSuper {
    @Override
    IMMOBILIZE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new IMMOBILIZE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {
    }

    @Override
    void eventHandlerTests() {
        PlayerMock target = mockServer.addPlayer();
        addEffect(target, 100, false);
        mockServer.getScheduler().performTicks(10);

        doOnPlayerInteractEventTest(target);
        doOnPlayerToggleFlightEventTest(target);
        // PlayerToggleSneakEvent not yet implemented in MockBukkit and including this causes the whole test to be skipped
        //doOnPlayerToggleSneakEventTest(target);
        doOnPlayerToggleSprintEventTest(target);
        doOnPlayerVelocityEventTest(target);
        doOnPlayerMoveEventTest(target);
        doOnPlayerMoveEventRotationOnlyTest(target);
    }

    /**
     * A block interaction by the immobilized player is cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerInteractEventTest(Player target) {
        Block block = testWorld.getBlockAt(origin);
        PlayerInteractEvent event = new PlayerInteractEvent(target, Action.RIGHT_CLICK_BLOCK, null, block, BlockFace.UP, null);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerInteractEvent not canceled by IMMOBILIZE");
    }

    /**
     * A flight toggle by the immobilized player is cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerToggleFlightEventTest(Player target) {
        PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(target, true);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerToggleFlightEvent not canceled by IMMOBILIZE");
    }

    /**
     * A sneak toggle by the immobilized player is cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerToggleSneakEventTest(Player target) {
        PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(target, true);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerToggleSneakEvent not canceled by IMMOBILIZE");
    }

    /**
     * A sprint toggle by the immobilized player is cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerToggleSprintEventTest(Player target) {
        PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(target, true);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerToggleSprintEvent not canceled by IMMOBILIZE");
    }

    /**
     * A velocity change on the immobilized player is cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerVelocityEventTest(Player target) {
        PlayerVelocityEvent event = new PlayerVelocityEvent(target, target.getVelocity());
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerVelocityEvent not canceled by IMMOBILIZE");
    }

    /**
     * A positional move by the immobilized player is cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerMoveEventTest(Player target) {
        Location from = target.getLocation();
        Location to = new Location(testWorld, from.getX() + 5, from.getY(), from.getZ());
        PlayerMoveEvent event = new PlayerMoveEvent(target, from, to);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerMoveEvent not canceled by IMMOBILIZE");
    }

    /**
     * A rotation-only move (pitch/yaw, same location) by the immobilized player is allowed, not cancelled.
     *
     * @param target the immobilized player
     */
    void doOnPlayerMoveEventRotationOnlyTest(Player target) {
        Location from = target.getLocation();
        Location to = from.clone();
        to.setPitch(from.getPitch() + 5);

        PlayerMoveEvent event = new PlayerMoveEvent(target, from, to);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);

        assertFalse(event.isCancelled(), "PlayerMoveEvent with rotation only canceled by IMMOBILIZE");
    }

    /**
     * A teleport or apparition (by coordinates or by name) of more than 100 blocks removes IMMOBILIZE; a shorter one
     * leaves it in place.
     */
    @Test
    void doOnPlayerTeleportEventsTests() {
        Location from = new Location(testWorld, 100, 40, 100);
        Location close = new Location(testWorld, 110, 40, 100);
        Location far = new Location(testWorld, 210, 40, 100);
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(from);

        // PlayerTeleportEvent
        IMMOBILIZE immobilize = (IMMOBILIZE) addEffect(target, 100, false);
        mockServer.getScheduler().performTicks(10);
        PlayerTeleportEvent teleportEvent = new PlayerTeleportEvent(target, from, close);
        mockServer.getPluginManager().callEvent(teleportEvent);
        mockServer.getScheduler().performTicks(5);
        assertFalse(immobilize.isKilled(), "effect canceled when player teleported < 100 blocks");
        teleportEvent = new PlayerTeleportEvent(target, from, far);
        mockServer.getPluginManager().callEvent(teleportEvent);
        mockServer.getScheduler().performTicks(5);
        assertTrue(immobilize.isKilled(), "effect not canceled when player teleported");

        immobilize = (IMMOBILIZE) addEffect(target, 100, false);
        mockServer.getScheduler().performTicks(10);
        OllivandersApparateByCoordinatesEvent apparateCoordEvent = new OllivandersApparateByCoordinatesEvent(target, close);
        mockServer.getPluginManager().callEvent(apparateCoordEvent);
        mockServer.getScheduler().performTicks(5);
        assertFalse(immobilize.isKilled(), "effect canceled when player apparated by coordinates < 100 blocks");
        apparateCoordEvent = new OllivandersApparateByCoordinatesEvent(target, far);
        mockServer.getPluginManager().callEvent(apparateCoordEvent);
        mockServer.getScheduler().performTicks(5);
        assertTrue(immobilize.isKilled(), "effect not canceled when player apparated by coordinates");

        immobilize = (IMMOBILIZE) addEffect(target, 100, false);
        mockServer.getScheduler().performTicks(10);
        OllivandersApparateByNameEvent apparateNameEvent = new OllivandersApparateByNameEvent(target, close, "to");
        mockServer.getPluginManager().callEvent(apparateNameEvent);
        mockServer.getScheduler().performTicks(5);
        assertFalse(immobilize.isKilled(), "effect canceled when player apparated by name < 100 blocks");
        apparateNameEvent = new OllivandersApparateByNameEvent(target, far, "to");
        mockServer.getPluginManager().callEvent(apparateNameEvent);
        mockServer.getScheduler().performTicks(5);
        assertTrue(immobilize.isKilled(), "effect not canceled when player apparated by name");
    }

    @Override
    void doRemoveTest() {
    }
}
