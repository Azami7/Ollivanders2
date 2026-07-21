package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.ACCIO;
import net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA;
import net.pottercraft.ollivanders2.spell.FIENDFYRE;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PROTEGO_HORRIBILIS}. Extends {@link O2StationarySpellTest} for the shared stationary-spell
 * tests.
 *
 * @author Azami7
 */
public class ProtegoHorribilisTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_HORRIBILIS;
    }

    @Override
    PROTEGO_HORRIBILIS createStationarySpell(Player caster, Location location) {
        return new PROTEGO_HORRIBILIS(testPlugin, caster.getUniqueId(), location, PROTEGO_HORRIBILIS.minRadiusConfig, PROTEGO_HORRIBILIS.minDurationConfig);
    }

    @Override @Test
    void upkeepTest() {
        // upkeep just calls age(), which is tested by ageAndKillTest()
    }

    /**
     * A Dark Arts spell (Fiendfyre) is cancelled crossing into the area, while Avada Kedavra and a non-Dark-Arts spell
     * (Accio) pass through.
     */
    @Test
    void doOnSpellProjectileMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_HORRIBILIS.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        PROTEGO_HORRIBILIS protegoHorribilis = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoHorribilis);
        mockServer.getScheduler().performTicks(20);

        caster.setLocation(location);
        player.setLocation(outsideLocation);
        assertFalse(protegoHorribilis.isLocationInside(player.getLocation()));

        // prevent dark arts spell from crossing spell boundary
        OllivandersSpellProjectileMoveEvent event = new OllivandersSpellProjectileMoveEvent(player, new FIENDFYRE(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "dark arts spell was not cancelled crossing inside spell area");

        // allow Avada Kadavra
        event = new OllivandersSpellProjectileMoveEvent(player, new AVADA_KEDAVRA(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "prevented avada kedavra from crossing spell boundary");

        // allow non-dark-arts spells
        event = new OllivandersSpellProjectileMoveEvent(player, new ACCIO(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "prevented accio from crossing spell boundary");
    }
}
