package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SUSPENSION;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SUSPENSION}.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
@Isolated
public class SuspensionTest extends EffectTestSuper {
    @Override
    SUSPENSION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SUSPENSION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * SUSPENSION hoists the player up, enables flying, and applies FLYING and FULL_IMMOBILIZE.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);

        SUSPENSION suspension = (SUSPENSION) addEffect(target, 500, false);

        // advance the server 5 ticks to ensure effect is active
        mockServer.getScheduler().performTicks(200);

        // confirm the effect thinks the player is suspended
        assertTrue(suspension.isSuspended(), "SUSPENSION did not suspend player");

        // confirm flying and immobilize effects were added
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FULL_IMMOBILIZE));
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FLYING));

        // confirm the player is actually suspended
        Location suspendLoc = target.getLocation();
        assertTrue(target.isFlying(), "SUSPENSION did not toggle flying for target");
        assertNotEquals(suspendLoc, origin, "SUSPENSION did not move player");
        assertTrue(suspendLoc.getY() > origin.getY(), "SUSPENSION did not move player up on Y axis");
    }

    @Override
    void eventHandlerTests() {
        doOnPlayerVelocityEventTest();
    }

    /**
     * SUSPENSION cancels PlayerVelocityEvent so the player cannot escape.
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
     * Removing SUSPENSION disables flying and removes the FLYING and FULL_IMMOBILIZE effects.
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

        // verify the player is no longer flying
        assertFalse(target.isFlying(), "Player is still flying after SUSPENSION was removed");

        // verify secondary effects were removed
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FULL_IMMOBILIZE),
            "FULL_IMMOBILIZE effect was not removed when SUSPENSION was removed");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FLYING),
            "FLYING effect was not removed when SUSPENSION was removed");
    }
}
