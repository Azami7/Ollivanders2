package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link NULLUM_APPAREBIT}. Extends {@link O2StationarySpellTest} for the shared stationary-spell tests.
 *
 * @author Azami7
 */
public class NullumApparebitTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.NULLUM_APPAREBIT;
    }

    @Override
    NULLUM_APPAREBIT createStationarySpell(Player caster, Location location) {
        return new NULLUM_APPAREBIT(testPlugin, caster.getUniqueId(), location, NULLUM_APPAREBIT.minRadiusConfig, NULLUM_APPAREBIT.minDurationConfig);
    }

    @Override @Test
    void upkeepTest() {
    }

    /**
     * Apparition into the area (by name and by coordinates) is blocked with feedback, while apparition out is allowed.
     */
    @Test
    void doOnOllivandersApparateEventsTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + NULLUM_APPAREBIT.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        NULLUM_APPAREBIT nullumApparebit = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumApparebit);
        mockServer.getScheduler().performTicks(20);

        // player cannot apparate in to spell area
        caster.setLocation(outsideLocation);
        assertFalse(nullumApparebit.isLocationInside(caster.getLocation()));
        OllivandersApparateByNameEvent event = new OllivandersApparateByNameEvent(caster, location, "inside");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "player allowed apparate in to spell area by name");

        // check player got feedback message
        mockServer.getScheduler().performTicks(200);
        String message = caster.nextMessage();
        assertNotNull(message, "Player did not get feedback message when trying to apparate in by name");
        assertEquals(NULLUM_APPAREBIT.feedbackMessage, TestCommon.cleanChatMessage(message), "player did not get expected feedback message");

        OllivandersApparateByCoordinatesEvent event2 = new OllivandersApparateByCoordinatesEvent(caster, location);
        mockServer.getPluginManager().callEvent(event2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event2.isCancelled(), "player allowed apparate in to spell area by coordinates");

        // check player got feedback message
        mockServer.getScheduler().performTicks(200);
        message = caster.nextMessage();
        assertNotNull(message, "Player did not get feedback message when trying to apparate in by name");
        assertEquals(NULLUM_APPAREBIT.feedbackMessage, TestCommon.cleanChatMessage(message), "player did not get expected feedback message");

        // player inside the spell area can apparate out
        caster.setLocation(location);
        assertTrue(nullumApparebit.isLocationInside(caster.getLocation()));
        event = new OllivandersApparateByNameEvent(caster, outsideLocation, "outside");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "player inside spell area prevented from apparating out by name");

        event2 = new OllivandersApparateByCoordinatesEvent(caster, outsideLocation);
        mockServer.getPluginManager().callEvent(event2);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event2.isCancelled(), "player inside spell area prevented from apparating out by coordinates");
    }

    /**
     * A non-player entity is blocked teleporting into the area but may teleport out.
     */
    @Test
    void doOnEntityTeleportEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + NULLUM_APPAREBIT.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        NULLUM_APPAREBIT nullumApparebit = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumApparebit);
        mockServer.getScheduler().performTicks(20);

        // entity outside spell area cannot teleport in
        Rabbit rabbit = testWorld.spawn(outsideLocation, Rabbit.class);
        assertFalse(nullumApparebit.isLocationInside(rabbit.getLocation()));
        EntityTeleportEvent event = new EntityTeleportEvent(rabbit, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "entity allowed to teleport in to spell area");

        // entity inside spell area can teleport out
        rabbit = testWorld.spawn(location, Rabbit.class);
        assertTrue(nullumApparebit.isLocationInside(rabbit.getLocation()));
        event = new EntityTeleportEvent(rabbit, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "entity inside spell area prevented from teleporting out");
    }

    /**
     * A player is blocked teleporting into the area (with feedback) but may teleport out.
     */
    @Test
    void doOnPlayerTeleportEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + NULLUM_APPAREBIT.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        NULLUM_APPAREBIT nullumApparebit = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumApparebit);
        mockServer.getScheduler().performTicks(20);

        // player outside the spell area cannot teleport in
        caster.setLocation(outsideLocation);
        assertFalse(nullumApparebit.isLocationInside(caster.getLocation()));
        PlayerTeleportEvent event = new PlayerTeleportEvent(caster, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "player allowed to teleport into spell area");

        // check player got feedback message
        mockServer.getScheduler().performTicks(200);
        String message = caster.nextMessage();
        assertNotNull(message, "Player did not get feedback message when trying to teleport in");
        assertEquals(NULLUM_APPAREBIT.feedbackMessage, TestCommon.cleanChatMessage(message), "player did not get expected feedback message");

        // player inside spell area can teleport out
        caster.setLocation(location);
        assertTrue(nullumApparebit.isLocationInside(caster.getLocation()));
        event = new PlayerTeleportEvent(caster, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "player not allowed to teleport out of spell area");
    }
}
