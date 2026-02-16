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
 * Abstract base test class for stationary spell implementations.
 *
 * <p>Provides common test suite for all concrete stationary spell types, testing core spell
 * functionality that applies across all spell implementations. Concrete test classes extend
 * this class and implement abstract methods to provide spell-specific setup and testing.</p>
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Duration management (clamping to min/max, increase/decrease operations)</li>
 *   <li>Radius management (clamping to min/max, increase/decrease operations)</li>
 *   <li>Spell aging and expiration (including permanent spell handling)</li>
 *   <li>Location-based area detection and entity radius queries</li>
 *   <li>Spell state management (active/killed status)</li>
 *   <li>Spell deserialization validation</li>
 *   <li>Spell-specific upkeep mechanics (implemented by subclasses)</li>
 * </ul>
 * </p>
 *
 * <p>Subclasses must implement:
 * <ul>
 *   <li>{@link #getSpellType()} - return the spell type to test</li>
 *   <li>{@link #createStationarySpell(Player, Location)} - create a spell instance for testing</li>
 *   <li>{@link #upkeepTest()} - test spell-specific upkeep behavior</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 */
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

    /**
     * Gets the spell type being tested by this test class.
     *
     * <p>Subclasses must override this to specify which spell type they test.</p>
     *
     * @return the O2StationarySpellType to test (not null)
     */
    abstract O2StationarySpellType getSpellType();

    /**
     * Creates a spell instance for testing.
     *
     * <p>Subclasses must override this to instantiate their specific spell type with
     * the given caster and location. The spell should be fully initialized and ready to test.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new spell instance (not null)
     */
    abstract O2StationarySpell createStationarySpell(Player caster, Location location);

    /**
     * Tests duration management for spells with variable lifetimes.
     *
     * <p>Verifies that spell duration is properly constrained between minimum and maximum values,
     * and that duration increases are clamped to the maximum allowed value. Skips testing for
     * permanent spells which ignore duration settings.</p>
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

    /**
     * Tests radius management and spell destruction when radius falls below minimum.
     *
     * <p>Verifies that spell radius is properly constrained between minimum and maximum values,
     * that radius increases are clamped to maximum, radius decreases are clamped to minimum,
     * and that the spell is killed if the radius is decreased below the minimum threshold.</p>
     */
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

    /**
     * Tests spell type getter (skipped - simple getter functionality).
     *
     * <p>The getSpellType() method is a trivial getter and is not tested here.
     * Correctness is verified by other tests that verify spell type assignments.</p>
     */
    @Test
    void getSpellTypeTest() {
        // simple getter, skipping
    }

    /**
     * Tests spell active state setter (skipped - simple setter functionality).
     *
     * <p>The setActive() method is a trivial setter and is not tested here.
     * Correctness is verified by other tests that depend on spell active state.</p>
     */
    @Test
    void setActiveTest() {
        // simple getter, skipping
    }

    /**
     * Tests spell aging mechanics and spell expiration leading to termination.
     *
     * <p>Verifies that the age() method properly decrements spell duration for non-permanent spells,
     * has no effect on permanent spells, and that spells are killed when their duration reaches zero.
     * Tests both single-tick aging and aging by multiple ticks.</p>
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

    /**
     * Tests percentage-based spell aging.
     *
     * <p>Verifies that the ageByPercent() method correctly reduces spell duration by the specified
     * percentage. Permanent spells should remain unaffected by percentage-based aging.</p>
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
            assertEquals(duration, stationarySpell.getDuration(), "");
        else
            assertEquals(newDuration, stationarySpell.getDuration(), "");
    }

    /**
     * Tests location-based area detection.
     *
     * <p>Verifies that isLocationInside() correctly identifies locations within the spell's
     * radius and correctly identifies locations outside the spell's radius.</p>
     */
    @Test
    void isLocationInsideTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        O2StationarySpell stationarySpell = createStationarySpell(player, location);

        assertTrue(stationarySpell.isLocationInside(location), "");
        assertFalse(stationarySpell.isLocationInside(new Location(location.getWorld(), location.getX(), location.getY() + stationarySpell.getMaxRadius() + 1, location.getZ())), "");
    }

    /**
     * Tests block retrieval getter (skipped - simple getter functionality).
     *
     * <p>The getBlock() method is a trivial getter and is not tested here.
     * Correctness is verified by other tests that depend on block queries.</p>
     */
    @Test
    void getBlockTest() {
        // simple getter, skipping
    }

    /**
     * Tests entity and player radius queries.
     *
     * <p>Verifies that getLivingEntitiesInsideSpellRadius() and getPlayersInsideSpellRadius() correctly
     * identify all entities and players within the spell's radius, and correctly exclude those outside the radius.
     * Tests with players and non-player entities to ensure proper filtering.</p>
     */
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

    /**
     * Tests spell flair/flavor effects (skipped - covered by common tests).
     *
     * <p>Flair functionality is tested comprehensively in Ollivanders2Common.flair tests
     * and is not duplicated here.</p>
     */
    @Test
    void flairTest() {
        // test covered by Ollivanders2Common.flair tests
    }

    /**
     * Tests caster ID getter (skipped - simple getter functionality).
     *
     * <p>The getCasterID() method is a trivial getter and is not tested here.
     * Correctness is verified by other tests that depend on caster identification.</p>
     */
    @Test
    void getCasterIDTest() {
        // simple getter, skipping
    }

    /**
     * Tests spell active state getter (skipped - simple getter functionality).
     *
     * <p>The isActive() method is a trivial getter and is not tested here.
     * Correctness is verified by other tests that depend on spell active status.</p>
     */
    @Test
    void isActiveTest() {
        // simple getter, skipping
    }

    /**
     * Tests spell killed state getter (skipped - simple getter functionality).
     *
     * <p>The isKilled() method is a trivial getter and is not tested here.
     * Correctness is verified by other tests that depend on spell kill status.</p>
     */
    @Test
    void isKilledTest() {
        // simple getter, skipping
    }

    /**
     * Tests spell location getter (skipped - simple getter functionality).
     *
     * <p>The getLocation() method is a trivial getter and is not tested here.
     * Correctness is verified by other tests that depend on spell location queries.</p>
     */
    @Test
    void getLocationTest() {
        // simple getter, skipping
    }

    /**
     * Tests spell-specific upkeep mechanics (implemented by subclasses).
     *
     * <p>Each spell type has unique upkeep behavior. Subclasses must implement this test
     * to verify their spell-specific per-tick processing and state updates.</p>
     */
    @Test
    abstract void upkeepTest();

    /**
     * Tests spell deserialization validation.
     *
     * <p>Verifies that a spell created via createStationarySpellByType() (simulating deserialization)
     * fails checkSpellDeserialization() because it lacks required data like location and caster,
     * while a properly initialized spell passes deserialization validation.</p>
     */
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
