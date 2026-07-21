package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for stationary spells, holding the tests common to every spell type. Concrete subclasses implement:
 * <ul>
 *   <li>{@link #getSpellType()} - the spell type under test</li>
 *   <li>{@link #createStationarySpell(Player, Location)} - build a spell instance to test</li>
 *   <li>{@link #upkeepTest()} - test the spell's own upkeep behavior</li>
 * </ul>
 *
 * @author Azami7
 */
abstract public class O2StationarySpellTest {
    static ServerMock mockServer;

    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's initial 20-tick delay so it is running
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * @return the spell type under test
     */
    abstract O2StationarySpellType getSpellType();

    /**
     * Build a spell instance to test.
     *
     * @param caster   the player casting the spell
     * @param location the center location of the spell
     * @return a new spell instance
     */
    abstract O2StationarySpell createStationarySpell(Player caster, Location location);

    /**
     * Duration stays within the min/max bounds and an increase is clamped to the maximum. Skipped for permanent spells.
     */
    @Test
    void durationTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        // skip test for permanent spells
        if (stationarySpell.isPermanent()) {
            return;
        }

        int duration = stationarySpell.getDuration();
        assertTrue(duration <= stationarySpell.getMaxDuration(), "Spell duration exceeds maximum");
        assertTrue(duration >= stationarySpell.getMinDuration(), "Spell duration below minimum");

        // increase the duration to just under the max
        int newDuration = stationarySpell.getMaxDuration() - 1;
        stationarySpell.increaseDuration(newDuration - duration); // increase by the difference
        assertEquals(newDuration, stationarySpell.getDuration(), "Duration not increased correctly");
        // increase over the max
        newDuration = stationarySpell.getMaxDuration() + 1;
        stationarySpell.increaseDuration(newDuration - duration); // increase by the difference
        assertEquals(stationarySpell.getMaxDuration(), stationarySpell.getDuration(), "Duration not clamped to maximum");
    }

    /**
     * Radius is clamped to the max when increased and to the min when decreased, and the spell is killed if decreased
     * below the minimum.
     */
    @Test
    void radiusTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        int radius = stationarySpell.getRadius();
        assertTrue(radius <= stationarySpell.getMaxRadius(), "Spell radius exceeds maximum");
        assertTrue(radius >= stationarySpell.getMinRadius(), "Spell radius below minimum");

        // increase to just over the max
        int newRadius = stationarySpell.getMaxRadius() + 1;
        stationarySpell.increaseRadius(newRadius - radius); // increase by difference
        assertEquals(stationarySpell.getMaxRadius(), stationarySpell.getRadius(), "Radius not clamped to maximum");

        // decrease to the min radius
        newRadius = stationarySpell.getMinRadius();
        stationarySpell.decreaseRadius(stationarySpell.getRadius() - newRadius); // decrease by difference
        assertEquals(stationarySpell.getMinRadius(), stationarySpell.getRadius(), "Radius not equal to minimum");

        // decrease below the min radius, this should cause the spell to be killed
        stationarySpell.decreaseRadius(1);
        assertTrue(stationarySpell.isKilled(), "Spell not killed when radius decreased below minimum");
    }

    @Test
    void getSpellTypeTest() {
        // simple getter, skipping
    }

    @Test
    void setActiveTest() {
        // simple setter, skipping
    }

    /**
     * Aging decrements a non-permanent spell's duration (by one tick and by many) and kills it at zero; a permanent
     * spell is unaffected.
     */
    @Test
    void ageAndKillTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        int duration = stationarySpell.getDuration();
        stationarySpell.age();
        if (stationarySpell.isPermanent())
            assertEquals(duration, stationarySpell.getDuration(), "Permanent spell duration changed after aging");
        else
            assertEquals(duration - 1, stationarySpell.getDuration(), "Spell duration not decreased by 1 tick");

        duration = stationarySpell.getDuration();
        stationarySpell.age(duration - 1);
        if (stationarySpell.isPermanent())
            assertEquals(duration, stationarySpell.getDuration(), "Permanent spell duration changed after aging multiple ticks");
        else
            assertEquals(1, stationarySpell.getDuration(), "Spell duration not correctly decreased by multiple ticks");

        stationarySpell.age(stationarySpell.getDuration());
        if (stationarySpell.isPermanent())
            assertFalse(stationarySpell.isKilled(), "Permanent spell was killed when duration reached zero");
        else
            assertTrue(stationarySpell.isKilled(), "Spell not killed when duration reached zero");
    }

    /**
     * Aging by a percent reduces duration by that fraction; a permanent spell is unaffected.
     */
    @Test
    void ageByPercentTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        int duration = stationarySpell.getDuration();
        double percent = 0.2;
        int newDuration = duration - ((int)(duration * percent));

        stationarySpell.ageByPercent(percent);
        if (stationarySpell.isPermanent())
            assertEquals(duration, stationarySpell.getDuration(), "Permanent spell duration changed after percentage aging");
        else
            assertEquals(newDuration, stationarySpell.getDuration(), "Spell duration not correctly reduced by percentage");
    }

    /**
     * A location within the radius reads as inside and one beyond it as outside.
     */
    @Test
    void isLocationInsideTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        assertTrue(stationarySpell.isLocationInside(location), "Spell location not detected as inside spell radius");
        assertFalse(stationarySpell.isLocationInside(new Location(location.getWorld(), location.getX(), location.getY() + stationarySpell.getMaxRadius() + 1, location.getZ())), "Location outside spell radius incorrectly detected as inside");
    }

    @Test
    void getBlockTest() {
        // simple getter, skipping
    }

    /**
     * The entity and player radius queries count only those inside the radius, distinguishing players from other
     * living entities.
     */
    @Test
    void entitiesRadiusTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        List<LivingEntity> entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertTrue(entitiesInRadius.isEmpty(), "Entities list should be empty before adding any entities");
        List<Player> playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertTrue(playersInRadius.isEmpty(), "Players list should be empty before adding any players");

        player.setLocation(location);
        entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertEquals(1, entitiesInRadius.size(), "Should have 1 entity in radius after placing player");
        playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertEquals(1, playersInRadius.size(), "Should have 1 player in radius after placing player");

        testWorld.spawn(location, Cow.class);
        entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertEquals(2, entitiesInRadius.size(), "Should have 2 entities in radius after spawning cow");
        playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertEquals(1, playersInRadius.size(), "Should still have only 1 player in radius after spawning non-player entity");

        PlayerMock player2 = mockServer.addPlayer();
        player2.setLocation(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + stationarySpell.getMaxRadius() + 1));
        entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertEquals(2, entitiesInRadius.size(), "Should still have 2 entities in radius (player2 is outside)");
        playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertEquals(1, playersInRadius.size(), "Should still have only 1 player in radius (player2 is outside)");
    }

    @Test
    void flairTest() {
        // test covered by Ollivanders2Common.flair tests
    }

    @Test
    void getCasterIDTest() {
        // simple getter, skipping
    }

    @Test
    void isActiveTest() {
        // simple getter, skipping
    }

    @Test
    void isKilledTest() {
        // simple getter, skipping
    }

    @Test
    void getLocationTest() {
        // simple getter, skipping
    }

    /**
     * Test this spell's own per-tick upkeep behavior.
     */
    @Test
    abstract void upkeepTest();

    /**
     * A spell built bare via createStationarySpellByType fails the deserialization check (no location or caster); a
     * fully constructed spell passes.
     */
    @Test
    void checkSpellDeserializationTest() {
        O2StationarySpell stationarySpell = Ollivanders2API.getStationarySpells().createStationarySpellByType(getSpellType());
        assertNotNull(stationarySpell);

        assertFalse(stationarySpell.checkSpellDeserialization(), "Deserialized spell should fail deserialization check without required data");

        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        stationarySpell = createStationarySpell(player, location);
        assertTrue(stationarySpell.checkSpellDeserialization(), "Failed to deserialize spell");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
