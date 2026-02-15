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

abstract public class O2StationarySpellTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    abstract O2StationarySpellType getSpellType();

    abstract O2StationarySpell createStationarySpell(Player caster, Location location);

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
        assertTrue(duration <= stationarySpell.getMaxDuration(), "");
        assertTrue(duration >= stationarySpell.getMinDuration(), "");

        // increase the duration to just under the max
        int newDuration = stationarySpell.getMaxDuration() - 1;
        stationarySpell.increaseDuration(newDuration - duration); // increase by the difference
        assertEquals(newDuration, stationarySpell.getDuration(), "");
        // increase over the max
        newDuration = stationarySpell.getMaxDuration() + 1;
        stationarySpell.increaseDuration(newDuration - duration); // increase by the difference
        assertEquals(stationarySpell.getMaxDuration(), stationarySpell.getDuration(), "");
    }

    @Test
    void radiusTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        int radius = stationarySpell.getRadius();
        assertTrue(radius <= stationarySpell.getMaxRadius(), "");
        assertTrue(radius >= stationarySpell.getMinRadius(), "");

        // increase to just over the max
        int newRadius = stationarySpell.getMaxRadius() + 1;
        stationarySpell.increaseRadius(newRadius - radius); // increase by difference
        assertEquals(stationarySpell.getMaxRadius(), stationarySpell.getRadius(), "");

        // decrease to the min radius
        newRadius = stationarySpell.getMinRadius();
        stationarySpell.decreaseRadius(stationarySpell.getRadius() - newRadius); // decrease by difference
        assertEquals(stationarySpell.getMinRadius(), stationarySpell.getRadius(), "");

        // decrease below the min radius, this should cause the spell to be killed
        stationarySpell.decreaseRadius(1);
        assertTrue(stationarySpell.isKilled(), "");
    }

    @Test
    void getSpellTypeTest() {
        // simple getter, skipping
    }

    @Test
    void setActiveTest() {
        // simple getter, skipping
    }

    @Test
    void ageAndKillTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        int duration = stationarySpell.getDuration();
        stationarySpell.age();
        if (stationarySpell.isPermanent())
            assertEquals(duration, stationarySpell.getDuration(), "");
        else
            assertEquals(duration - 1, stationarySpell.getDuration(), "");

        duration = stationarySpell.getDuration();
        stationarySpell.age(duration - 1);
        if (stationarySpell.isPermanent())
            assertEquals(duration, stationarySpell.getDuration(), "");
        else
            assertEquals(1, stationarySpell.getDuration(), "");

        stationarySpell.age(stationarySpell.getDuration());
        if (stationarySpell.isPermanent())
            assertFalse(stationarySpell.isKilled(), "");
        else
            assertTrue(stationarySpell.isKilled(), "");
    }

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
            assertEquals(duration, stationarySpell.getDuration(), "");
        else
            assertEquals(newDuration, stationarySpell.getDuration(), "");
    }

    @Test
    void isLocationInsideTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        assertTrue(stationarySpell.isLocationInside(location), "");
        assertFalse(stationarySpell.isLocationInside(new Location(location.getWorld(), location.getX(), location.getY() + stationarySpell.getMaxRadius() + 1, location.getZ())), "");
    }

    @Test
    void getBlockTest() {
        // simple getter, skipping
    }

    @Test
    void entitiesRadiusTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        List<LivingEntity> entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertTrue(entitiesInRadius.isEmpty(), "");
        List<Player> playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertTrue(playersInRadius.isEmpty(), "");

        player.setLocation(location);
        entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertEquals(1, entitiesInRadius.size(), "");
        playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertEquals(1, playersInRadius.size(), "");

        testWorld.spawn(location, Cow.class);
        entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertEquals(2, entitiesInRadius.size(), "");
        playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertEquals(1, playersInRadius.size(), "");

        PlayerMock player2 = mockServer.addPlayer();
        player2.setLocation(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + stationarySpell.getMaxRadius() + 1));
        entitiesInRadius = stationarySpell.getLivingEntitiesInsideSpellRadius();
        assertEquals(2, entitiesInRadius.size(), "");
        playersInRadius = stationarySpell.getPlayersInsideSpellRadius();
        assertEquals(1, playersInRadius.size(), "");
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

    @Test
    abstract void upkeepTest();

    @Test
    void checkSpellDeserializationTest() {
        O2StationarySpell stationarySpell = Ollivanders2API.getStationarySpells().createStationarySpellByType(getSpellType());
        assertNotNull(stationarySpell);

        assertFalse(stationarySpell.checkSpellDeserialization(), "");

        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        stationarySpell = createStationarySpell(player, location);
        assertTrue(stationarySpell.checkSpellDeserialization(), "Failed to deserialize spell");
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
