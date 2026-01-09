package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SUSPENSION;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the SUSPENSION effect.
 *
 * <p>SUSPENSION is a debilitating effect that hoists the target player into the air and maintains them
 * in suspension by applying secondary effects (FLYING and IMMOBILIZE). This test class verifies that the
 * effect correctly suspends the player, applies necessary secondary effects, prevents movement via event
 * cancellation, and properly cleans up when the effect expires.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Suspension activation - verifies the player is hoisted into the air and marked as suspended</li>
 * <li>Secondary effect application - verifies FLYING and IMMOBILIZE effects are applied to maintain suspension</li>
 * <li>Flying state - verifies the player's flying state is enabled during suspension</li>
 * <li>Velocity event handling - verifies PlayerVelocityEvent is cancelled to prevent escape from suspension</li>
 * <li>Location restoration - verifies the player is returned to original location when effect is removed</li>
 * <li>State cleanup - verifies flying is disabled and secondary effects are removed upon effect expiration</li>
 * </ul>
 *
 * @author Azami7
 * @see SUSPENSION for the effect implementation being tested
 * @see EffectTestSuper for the base testing framework
 */
public class SuspensionTest extends EffectTestSuper {
    @Override
    SUSPENSION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SUSPENSION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Verify that the SUSPENSION effect correctly suspends the player.
     *
     * <p>Tests that when a SUSPENSION effect is applied, the player is properly hoisted into the air,
     * the flying state is enabled to prevent falling, and secondary effects (FLYING and IMMOBILIZE)
     * are applied to maintain the suspension state.</p>
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);

        SUSPENSION suspension = (SUSPENSION) addEffect(target, 100, false);

        // advance the server 5 ticks to ensure effect is active
        mockServer.getScheduler().performTicks(5);

        // confirm the effect thinks the player is suspended
        assertTrue(suspension.isSuspended(), "SUSPENSION did not suspend player");

        // confirm flying and immobilize effects were added
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE));
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FLYING));

        // confirm the player is actually suspended
        Location suspendLoc = target.getLocation();
        assertTrue(target.isFlying(), "SUSPENSION did not toggle flying for target");
        assertTrue(suspendLoc.getY() > origin.getY(), "SUSPENSION did not move player up on Y axis");
    }

    @Override
    void eventHandlerTests() {
        doOnPlayerVelocityEventTest();
    }

    /**
     * Verify that SUSPENSION cancels PlayerVelocityEvent to prevent escape from suspension.
     *
     * <p>Tests that when a player velocity event occurs while SUSPENSION is active, the event
     * is cancelled to prevent the player from breaking free of the suspension state through
     * velocity changes.</p>
     */
    void doOnPlayerVelocityEventTest() {
        PlayerMock target = mockServer.addPlayer();
        addEffect(target, 100, false);

        PlayerVelocityEvent event = new PlayerVelocityEvent(target, target.getVelocity());
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerVelocityEvent was not canceled by SUSPENSION");
    }

    /**
     * Verify that SUSPENSION properly cleans up when the effect is removed.
     *
     * <p>Tests that when the SUSPENSION effect is removed, the player is returned to their original
     * location, the flying state is disabled, and all secondary effects (FLYING and IMMOBILIZE)
     * are properly cleaned up to restore the player to their normal state.</p>
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);

        SUSPENSION suspension = (SUSPENSION) addEffect(target, 100, false);

        // advance the server 5 ticks to ensure the suspension effect is active
        mockServer.getScheduler().performTicks(5);

        // call doRemove() to clean up
        suspension.doRemove();

        // verify the player was returned to original location
        Location returnedLoc = target.getLocation();
        assertTrue(returnedLoc.getX() == origin.getX() && returnedLoc.getY() == origin.getY() && returnedLoc.getZ() == origin.getZ(),
            "Player was not returned to original location. Expected (" + origin.getX() + ", " + origin.getY() + ", " + origin.getZ() + "), got (" + returnedLoc.getX() + ", " + returnedLoc.getY() + ", " + returnedLoc.getZ() + ")");

        // verify the player is no longer flying
        assertFalse(target.isFlying(), "Player is still flying after SUSPENSION was removed");

        // verify secondary effects were removed
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE),
            "IMMOBILIZE effect was not removed when SUSPENSION was removed");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FLYING),
            "FLYING effect was not removed when SUSPENSION was removed");
    }
}
