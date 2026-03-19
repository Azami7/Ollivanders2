package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.INVISIBILITY;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the INVISIBILITY effect.
 *
 * <p>Validates that the INVISIBILITY effect correctly hides the target player from all online
 * players, hides the target from newly joining players via the PlayerJoinEvent handler, and
 * restores visibility when the effect is removed.</p>
 *
 * @see INVISIBILITY
 * @see EffectTestSuper
 */
public class InvisibilityTest extends EffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    INVISIBILITY createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new INVISIBILITY(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test that checkEffect hides the target player from all online players.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        PlayerMock viewer = mockServer.addPlayer();

        // viewer can see target before the effect
        assertTrue(viewer.canSee(target), "Viewer should see target before invisibility effect");

        addEffect(target, 1000, false);

        // advance ticks so checkEffect runs and makeInvisible is called
        mockServer.getScheduler().performTicks(5);

        assertFalse(viewer.canSee(target), "Viewer should not see target after invisibility effect is active");

        // clean up
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), O2EffectType.INVISIBILITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void eventHandlerTests() {
        doOnPlayerJoinEventTest();
    }

    /**
     * Test that doOnPlayerJoinEvent hides the invisible target from joining players and does not
     * affect visibility of non-invisible players.
     */
    private void doOnPlayerJoinEventTest() {
        PlayerMock target = mockServer.addPlayer();
        PlayerMock nonInvisiblePlayer = mockServer.addPlayer();

        addEffect(target, 1000, false);

        // advance ticks so the effect becomes active (invisible = true)
        mockServer.getScheduler().performTicks(5);

        // add a new player and directly fire the PlayerJoinEvent
        PlayerMock joiner = mockServer.addPlayer();
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(joiner, "joined");
        mockServer.getPluginManager().callEvent(joinEvent);
        mockServer.getScheduler().performTicks(1);

        // joiner should not see the invisible target
        assertFalse(joiner.canSee(target), "Joining player should not see invisible target");

        // joiner should still see the player without the invisibility effect
        assertTrue(joiner.canSee(nonInvisiblePlayer), "Joining player should still see non-invisible players");

        // clean up
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), O2EffectType.INVISIBILITY);
    }

    /**
     * Test that doRemove restores the target player's visibility to all online players.
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();
        PlayerMock viewer = mockServer.addPlayer();

        addEffect(target, 1000, false);

        // advance ticks so the effect becomes active
        mockServer.getScheduler().performTicks(5);
        assertFalse(viewer.canSee(target), "Viewer should not see target while invisible");

        // remove the effect - should restore visibility
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), O2EffectType.INVISIBILITY);
        mockServer.getScheduler().performTicks(1);

        assertTrue(viewer.canSee(target), "Viewer should see target after invisibility effect is removed");
    }
}
