package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.stationaryspell.ConcealmentShieldSpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link REPELLO_MUGGLETON}. Extends {@link ConcealmentShieldSpellTest} for the shared
 * concealment-shield tests.
 */
public class RepelloMuggletonTest extends ConcealmentShieldSpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.REPELLO_MUGGLETON;
    }

    @Override
    REPELLO_MUGGLETON createStationarySpell(Player caster, Location location) {
        return new REPELLO_MUGGLETON(testPlugin, caster.getUniqueId(), location, defaultRadius, defaultDuration);
    }

    /**
     * A muggle is blocked from entering the area and gets an entry-deny message, while a non-muggle (including the
     * caster) enters freely. Overrides the parent test because only muggles are affected.
     */
    @Override @Test
    void doOnPlayerMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        O2Player casterO2Player = testPlugin.getO2Player(caster);
        assertNotNull(casterO2Player, "Failed to find O2Player");
        casterO2Player.setMuggle(false);

        // create concealment shield spell
        REPELLO_MUGGLETON repelloMuggleton = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(repelloMuggleton);

        Location outsideLocation = new Location(location.getWorld(), location.getX() + repelloMuggleton.getMaxRadius() + 1, location.getY(), location.getZ());
        assertFalse(repelloMuggleton.isLocationInside(outsideLocation));

        // caster can enter their own spell area
        caster.setLocation(outsideLocation);
        mockServer.getScheduler().performTicks(20);
        caster.setLocation(location);

        PlayerMoveEvent event = new PlayerMoveEvent(caster, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled());

        // prevent a muggle from entering the area
        PlayerMock muggle = mockServer.addPlayer();
        O2Player muggleO2Player = testPlugin.getO2Player(muggle);
        assertNotNull(muggleO2Player, "Failed to find O2Player");
        muggleO2Player.setMuggle(true);
        assertFalse(repelloMuggleton.canEnter(muggle), "stationarySpell.canEnter() returned true for muggle");

        muggle.setLocation(outsideLocation);
        event = new PlayerMoveEvent(muggle, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "Muggle move event into spell area was not cancelled");
        String message = muggle.nextMessage();
        assertNotNull(message, "Muggle did not receive entry denial message");
        TestCommon.containsStringMatch(repelloMuggleton.getEntryDenyMessages(), message);

        // allow non-muggle to enter
        muggleO2Player.setMuggle(false);
        assertTrue(repelloMuggleton.canEnter(muggle), "stationarySpell.canEnter() returned false for non-muggle");
        event = new PlayerMoveEvent(muggle, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "Non-muggle move event into spell area was cancelled");
    }

    /**
     * A wizard is hidden from an outside muggle while inside the area and visible again once they leave.
     */
    @Override @Test
    void doOnEntityTargetEventTestVisibilityCheck() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        O2Player casterO2Player = testPlugin.getO2Player(caster);
        assertNotNull(casterO2Player, "Failed to find O2Player");
        casterO2Player.setMuggle(false);

        // create concealment shield spell
        ConcealmentShieldSpell repelloMuggleton = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(repelloMuggleton);
        Location outsideLocation = new Location(location.getWorld(), location.getX() + repelloMuggleton.getMaxRadius() + 1, location.getY(), location.getZ());

        // add a muggle player outside the spell area
        PlayerMock muggle = mockServer.addPlayer();
        O2Player muggleO2Player = testPlugin.getO2Player(muggle);
        assertNotNull(muggleO2Player, "Failed to find O2Player");
        muggleO2Player.setMuggle(true);
        muggle.setLocation(outsideLocation);
        assertFalse(repelloMuggleton.isLocationInside(muggle.getLocation()));

        // ensure MockBukkit hidePlayer/canSee work
        muggle.hidePlayer(testPlugin, caster);
        assertFalse(muggle.canSee(caster), "caster still visible after muggle.hidePlayer(testPlugin, caster)");
        muggle.showPlayer(testPlugin, caster);
        assertTrue(muggle.canSee(caster), "caster not visible after muggle.showPlayer(testPlugin, caster)");

        // put player outside location and move them inside
        caster.setLocation(outsideLocation);
        assertFalse(repelloMuggleton.isLocationInside(caster.getLocation()), "Caster is not starting outside the spell area.");
        assertTrue(muggle.canSee(caster), "Muggle cannot see caster outside of the spell area");
        caster.setLocation(location);
        PlayerMoveEvent event = new PlayerMoveEvent(caster, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "PlayerMoveEvent canceled unexpectedly");
        assertFalse(repelloMuggleton.getPlayersInsideSpellRadius().isEmpty(), "No player in spell area");
        assertTrue(repelloMuggleton.isLocationInside(caster.getLocation()), "Caster did not move to a location inside the spell area.");
        // player should now be hidden from player2
        assertFalse(muggle.canSee(caster), "Muggle can still see caster inside spell area");

        // move player back outside the spell
        caster.setLocation(outsideLocation);
        event = new PlayerMoveEvent(caster, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 10);
        // player2 can now see player
        assertFalse(repelloMuggleton.isLocationInside(caster.getLocation()));
        assertTrue(muggle.canSee(caster), "Muggle cannot see caster outside of spell area");
    }

    /**
     * Repello muggleton does not have a proximity alarm
     */
    @Override
    void checkProximityAlarm(PlayerMock caster, ConcealmentShieldSpell spell) {}
}
