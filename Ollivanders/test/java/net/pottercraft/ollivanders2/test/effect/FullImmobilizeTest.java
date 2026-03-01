package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FULL_IMMOBILIZE;
import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the FULL_IMMOBILIZE effect.
 *
 * <p>FULL_IMMOBILIZE is an enhanced variant of IMMOBILIZE that prevents all player movement,
 * including rotation-only changes. While IMMOBILIZE allows players to rotate their view,
 * FULL_IMMOBILIZE blocks even rotation-only movement events to ensure complete immobilization.
 * Additionally, FULL_IMMOBILIZE cancels all teleport and apparate attempts (both short and long
 * distance), whereas IMMOBILIZE only blocks short-distance attempts and allows long-distance
 * escape (> 100 blocks).</p>
 *
 * <p>Inherits all test coverage from {@link ImmobolizeTest} and adds specific tests for
 * rotation-only movement prevention and complete teleportation blocking.</p>
 */
public class FullImmobilizeTest extends ImmobolizeTest {
    /**
     * Creates a FULL_IMMOBILIZE effect for testing.
     *
     * <p>Instantiates a new FULL_IMMOBILIZE effect with the specified parameters.
     * This method is called by the inherited test methods to create fresh effect instances.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new FULL_IMMOBILIZE effect
     */
    @Override
    IMMOBILIZE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FULL_IMMOBILIZE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Tests that FULL_IMMOBILIZE blocks rotation-only movement.
     *
     * <p>Verifies that a PlayerMoveEvent with only rotation changes (pitch change, same position)
     * is cancelled by the FULL_IMMOBILIZE effect. This is the key difference between FULL_IMMOBILIZE
     * and regular IMMOBILIZE, which allows rotation-only changes.</p>
     *
     * @param target the immobilized player
     */
    @Override
    void doOnPlayerMoveEventRotationOnlyTest(Player target) {
        Location from = target.getLocation();
        Location to = new Location(testWorld, from.getX(), from.getY(), from.getZ());
        to.setPitch(from.getPitch() + 5);

        PlayerMoveEvent event = new PlayerMoveEvent(target, from, to);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);

        assertTrue(event.isCancelled(), "PlayerMoveEvent with rotation only not canceled by FULL_IMMOBILIZE");
    }

    /**
     * Tests that FULL_IMMOBILIZE cancels all teleport and apparate attempts.
     *
     * <p>Verifies that FULL_IMMOBILIZE blocks teleportation magic completely, preventing both
     * Bukkit teleports and Ollivanders apparate spells regardless of distance. Unlike IMMOBILIZE,
     * which allows long-distance escapes (> 100 blocks), FULL_IMMOBILIZE traps the player completely
     * with no magical escape possible.</p>
     */
    @Override
    @Test
    void doOnPlayerTeleportEventsTests() {
        Location from = new Location(testWorld, 100, 40, 100);
        Location to = new Location(testWorld, 110, 40, 100);

        PlayerMock target = mockServer.addPlayer();
        IMMOBILIZE immobilize = (IMMOBILIZE) addEffect(target, 100, false);
        mockServer.getScheduler().performTicks(10);

        PlayerTeleportEvent event = new PlayerTeleportEvent(target, from, to);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "teleport event not canceled");

        OllivandersApparateByCoordinatesEvent apparateCoordEvent = new OllivandersApparateByCoordinatesEvent(target, to);
        mockServer.getPluginManager().callEvent(apparateCoordEvent);
        mockServer.getScheduler().performTicks(5);
        assertTrue(apparateCoordEvent.isCancelled(), "apparate by coord event not canceled");

        OllivandersApparateByNameEvent apparateNameEvent = new OllivandersApparateByNameEvent(target, to, "to");
        mockServer.getPluginManager().callEvent(apparateNameEvent);
        mockServer.getScheduler().performTicks(5);
        assertTrue(apparateNameEvent.isCancelled(), "apparate by name event not canceled");
    }
}
