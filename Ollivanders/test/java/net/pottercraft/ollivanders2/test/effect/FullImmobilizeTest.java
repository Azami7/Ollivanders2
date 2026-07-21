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
 * Unit tests for {@link net.pottercraft.ollivanders2.effect.FULL_IMMOBILIZE}. Inherits IMMOBILIZE coverage and
 * overrides the rotation-only and teleport cases, which FULL_IMMOBILIZE blocks even when IMMOBILIZE allows them.
 *
 * @see ImmobolizeTest
 */
public class FullImmobilizeTest extends ImmobolizeTest {
    @Override
    IMMOBILIZE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FULL_IMMOBILIZE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * A rotation-only move (pitch change, same position) is cancelled; IMMOBILIZE would allow it.
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
     * All teleport and apparate attempts are cancelled regardless of distance; IMMOBILIZE allows long-distance escapes.
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
