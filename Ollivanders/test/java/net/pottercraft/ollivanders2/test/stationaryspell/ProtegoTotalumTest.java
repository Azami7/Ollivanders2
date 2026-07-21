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
 * Unit tests for {@link PROTEGO_TOTALUM}. Extends {@link O2StationarySpellTest} for the shared stationary-spell tests.
 */
public class ProtegoTotalumTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_TOTALUM;
    }

    @Override
    PROTEGO_TOTALUM createStationarySpell(Player caster, Location location) {
        return new PROTEGO_TOTALUM(testPlugin, caster.getUniqueId(), location, PROTEGO_TOTALUM.minRadiusConfig, PROTEGO_TOTALUM.minDurationConfig);
    }

    /**
     * Upkeep disables and tracks the AI of a hostile mob that enters the area, leaves a passive mob's AI alone, and
     * leaves mobs outside the area untouched.
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
     * A player is blocked moving from outside into the area but may move from inside out.
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
     * A creature spawning inside the area has its spawn cancelled and the entity removed.
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
     * When the spell ends, AI is restored to still-living affected entities and a dead tracked entity causes no error.
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
