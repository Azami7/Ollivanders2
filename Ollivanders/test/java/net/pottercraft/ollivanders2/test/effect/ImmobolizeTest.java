package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the IMMOBILIZE effect.
 *
 * <p>IMMOBILIZE is a complete immobilization effect that prevents all player movement and actions.
 * This test validates that the effect correctly cancels all movement-related events and interaction
 * events to ensure the immobilized player cannot perform any action.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>PlayerInteractEvent cancellation - ensures players cannot interact with the environment</li>
 * <li>PlayerToggleFlightEvent cancellation - prevents flight toggling</li>
 * <li>PlayerToggleSneakEvent cancellation - prevents sneak toggling</li>
 * <li>PlayerToggleSprintEvent cancellation - prevents sprint toggling</li>
 * <li>PlayerVelocityEvent cancellation - prevents velocity-based movement</li>
 * <li>PlayerMoveEvent cancellation - prevents direct player movement</li>
 * </ul>
 *
 * @author Azami7
 * @see IMMOBILIZE for the effect implementation being tested
 * @see EffectTestSuper for the base testing framework
 */
public class ImmobolizeTest extends EffectTestSuper {
    /**
     * Create an IMMOBILIZE effect for testing.
     *
     * <p>Instantiates a new IMMOBILIZE effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new IMMOBILIZE effect targeting the specified player
     */
    IMMOBILIZE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new IMMOBILIZE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test basic IMMOBILIZE effect behavior.
     *
     * <p>IMMOBILIZE is a passive effect that only prevents movement through event cancellation.
     * This test does not need to verify any active behavior during checkEffect(), as all
     * immobilization is handled through event handlers.</p>
     */
    void checkEffectTest() {
    }

    /**
     * Run all event handler tests for the IMMOBILIZE effect.
     *
     * <p>Tests all movement and interaction prevention mechanisms by creating an immobilized player
     * and firing various events to verify they are properly cancelled.</p>
     */
    void eventHandlerTests() {
        PlayerMock target = mockServer.addPlayer();
        IMMOBILIZE immobilize = (IMMOBILIZE) addEffect(target, 100, false);
        mockServer.getScheduler().performTicks(10);

        doOnPlayerInteractEventTest(target);
        doOnPlayerToggleFlightEventTest(target);
        // PlayerToggleSneakEvent not yet implemented in MockBukkit and including this causes the whole test to be skipped
        //doOnPlayerToggleSneakEventTest(target);
        doOnPlayerToggleSprintEventTest(target);
        doOnPlayerVelocityEventTest(target);
        doOnPlayerMoveEventTest(target);
    }

    /**
     * Test that PlayerInteractEvent is cancelled by IMMOBILIZE.
     *
     * <p>Verifies that immobilized players cannot interact with blocks, items, or other objects
     * in the world. The test creates a block interaction event and ensures it is cancelled.</p>
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
     * Test that PlayerToggleFlightEvent is cancelled by IMMOBILIZE.
     *
     * <p>Verifies that immobilized players cannot toggle flight mode. The test creates a flight
     * toggle event and ensures it is cancelled, preventing the player from enabling or disabling flight.</p>
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
     * Test that PlayerToggleSneakEvent is cancelled by IMMOBILIZE.
     *
     * <p>Verifies that immobilized players cannot toggle sneak mode. The test creates a sneak
     * toggle event and ensures it is cancelled, preventing the player from changing their sneak state.</p>
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
     * Test that PlayerToggleSprintEvent is cancelled by IMMOBILIZE.
     *
     * <p>Verifies that immobilized players cannot toggle sprint mode. The test creates a sprint
     * toggle event and ensures it is cancelled, preventing the player from activating or deactivating sprinting.</p>
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
     * Test that PlayerVelocityEvent is cancelled by IMMOBILIZE.
     *
     * <p>Verifies that immobilized players cannot be moved by velocity-changing mechanisms such as
     * explosions, knockback, or other forces. The test creates a velocity event and ensures it is
     * cancelled, preventing any velocity-based movement.</p>
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
     * Test that PlayerMoveEvent is cancelled by IMMOBILIZE.
     *
     * <p>Verifies that immobilized players cannot move from their current location. The test creates
     * a move event where the player attempts to move 5 blocks away and ensures it is cancelled,
     * keeping the immobilized player in place.</p>
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
     * Test IMMOBILIZE effect cleanup.
     *
     * <p>The IMMOBILIZE effect has no persistent state to clean up when removed. All immobilization
     * is handled through event cancellation, so there is nothing to verify during cleanup.</p>
     */
    void doRemoveTest() {
    }
}
