package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link PROTEGO_TOTALUM} stationary spell.
 *
 * <p>Tests the protego totalum protective barrier spell, which prevents entities from entering
 * the protected area by:
 * <ul>
 *   <li>Disabling AI on hostile mobs that enter the spell area</li>
 *   <li>Blocking player movement into the protected area</li>
 *   <li>Preventing creature spawns inside the protected area</li>
 *   <li>Restoring AI to affected entities when the spell ends</li>
 * </ul>
 * Inherits common spell tests from {@link O2StationarySpellTest} and provides spell-specific
 * factory methods for test setup.</p>
 *
 * @author Test Author
 */
public class ProtegoTotalumTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#PROTEGO_TOTALUM}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_TOTALUM;
    }

    /**
     * Creates a PROTEGO_TOTALUM spell instance for testing.
     *
     * <p>Constructs a new protego totalum spell at the specified location with the minimum radius and duration values.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new PROTEGO_TOTALUM spell instance (not null)
     */
    @Override
    PROTEGO_TOTALUM createStationarySpell(Player caster, Location location) {
        return new PROTEGO_TOTALUM(testPlugin, caster.getUniqueId(), location, PROTEGO_TOTALUM.minRadiusConfig, PROTEGO_TOTALUM.minDurationConfig);
    }

    /**
     * Tests upkeep behavior and hostile mob AI management.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Hostile mobs outside the spell area are unaffected</li>
     *   <li>Hostile mobs inside the spell area have their AI disabled and are tracked</li>
     *   <li>Non-hostile mobs (passive mobs) inside the spell area are not affected</li>
     * </ul>
     * </p>
     */
    @Override
    @Test
    void upkeepTest() {
        // age() is tested by ageAndKillTest, this will test the hostile mob management
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_TOTALUM.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        Skeleton skeleton = testWorld.spawn(outsideLocation, Skeleton.class);

        PROTEGO_TOTALUM protegoTotalum = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoTotalum);
        mockServer.getScheduler().performTicks(20);

        // hostile mob outside the spell area is unaffected
        assertTrue(protegoTotalum.getTrackedAffectedEntities().isEmpty());
        assertTrue(skeleton.hasAI());

        // hostile mob in the spell area has AI removed so it will no longer move or attack
        skeleton.teleport(location);
        mockServer.getScheduler().performTicks(20);
        assertTrue(protegoTotalum.isLocationInside(skeleton.getLocation()));
        assertFalse(protegoTotalum.getTrackedAffectedEntities().isEmpty(), "Skeleton in spell area not being tracked");
        assertFalse(skeleton.hasAI(), "Skeleton AI not disabled in spell area");

        // non-hostile mob is not affected
        Cow cow = testWorld.spawn(outsideLocation, Cow.class);
        cow.teleport(location);
        assertTrue(protegoTotalum.isLocationInside(cow.getLocation()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(cow.hasAI(), "Cow AI removed in spell area");
    }

    /**
     * Tests player movement blocking at the spell boundary.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Players cannot move from outside the spell area to inside it</li>
     *   <li>Players can move from inside the spell area to outside it</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnPlayerMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_TOTALUM.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_TOTALUM protegoTotalum = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoTotalum);
        mockServer.getScheduler().performTicks(20);

        // player cannot move from outside the spell area to inside it
        caster.setLocation(outsideLocation);
        PlayerMoveEvent event = new PlayerMoveEvent(caster, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "player move event from outside spell area inside was not cancelled");

        // player can move from inside the spell area to outside it
        caster.setLocation(location);
        event = new PlayerMoveEvent(caster, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "player prevented from leaving the spell area");
    }

    /**
     * Tests creature spawn blocking inside the spell area.
     *
     * <p>Verifies that creatures attempting to spawn inside the protected area have their spawn
     * event cancelled and the entity is removed.</p>
     */
    @Test
    void doOnCreatureSpawnEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_TOTALUM protegoTotalum = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoTotalum);
        mockServer.getScheduler().performTicks(20);

        Cow cow = testWorld.spawn(location, Cow.class);
        CreatureSpawnEvent event = new CreatureSpawnEvent(cow, CreatureSpawnEvent.SpawnReason.DEFAULT);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "cow allowed to spawn in spell area");
        assertTrue(cow.isDead(), "cow entity not removed");
    }

    /**
     * Tests cleanup behavior when the spell ends.
     *
     * <p>Verifies that when the spell is killed, AI is restored to affected entities that are
     * still alive. Also tests that dead entities in the tracked list do not cause issues during cleanup.</p>
     */
    @Test
    void doCleanUpTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_TOTALUM.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_TOTALUM protegoTotalum = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoTotalum);
        mockServer.getScheduler().performTicks(20);

        Skeleton skeleton1 = testWorld.spawn(outsideLocation, Skeleton.class);
        Skeleton skeleton2 = testWorld.spawn(outsideLocation, Skeleton.class);

        skeleton1.teleport(location);
        skeleton2.teleport(location);
        mockServer.getScheduler().performTicks(20);

        assertEquals(2, protegoTotalum.getTrackedAffectedEntities().size());

        skeleton1.remove(); // kill skeleton1 so that we have a dead and not dead entity in the list
        protegoTotalum.kill();
        mockServer.getScheduler().performTicks(20);

        assertTrue(skeleton2.hasAI(), "Skeleton2 AI not restored when spell ended");
    }
}
