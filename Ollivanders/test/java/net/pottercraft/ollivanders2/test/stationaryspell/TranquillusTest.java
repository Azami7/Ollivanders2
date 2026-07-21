package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.TRANQUILLUS;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TRANQUILLUS}. Extends {@link O2StationarySpellTest} for the shared stationary-spell tests.
 */
public class TranquillusTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.TRANQUILLUS;
    }

    @Override
    TRANQUILLUS createStationarySpell(Player caster, Location location) {
        return new TRANQUILLUS(testPlugin, caster.getUniqueId(), location, TRANQUILLUS.minRadiusConfig, TRANQUILLUS.minDurationConfig);
    }

    @Override
    @Test
    void upkeepTest() {
        // upkeep only does age, which is covered by ageAndKillTest()
    }

    /**
     * Target acquisition is cancelled for an entity inside the area and allowed for one outside.
     */
    @Test
    void doOnEntityTargetEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + TRANQUILLUS.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(location);

        TRANQUILLUS tranquillus = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(tranquillus);
        mockServer.getScheduler().performTicks(20);

        // entity inside spell area cannot target
        Skeleton skeleton = testWorld.spawn(location, Skeleton.class);
        assertTrue(tranquillus.isLocationInside(skeleton.getLocation()));
        EntityTargetEvent event = new EntityTargetEvent(skeleton, target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "EntityTargetEvent not cancelled inside tranquillus area");

        // entity outside spell area can target normally
        Skeleton outsideSkeleton = testWorld.spawn(outsideLocation, Skeleton.class);
        assertFalse(tranquillus.isLocationInside(outsideSkeleton.getLocation()));
        EntityTargetEvent outsideEvent = new EntityTargetEvent(outsideSkeleton, target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
        mockServer.getPluginManager().callEvent(outsideEvent);
        mockServer.getScheduler().performTicks(5);
        assertFalse(outsideEvent.isCancelled(), "EntityTargetEvent cancelled outside tranquillus area");
    }

    /**
     * A projectile launched from inside the area is cancelled and one launched from outside is allowed.
     */
    @Test
    void doOnProjectileLaunchEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + TRANQUILLUS.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        TRANQUILLUS tranquillus = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(tranquillus);
        mockServer.getScheduler().performTicks(20);

        // projectile launched inside spell area is cancelled
        assertTrue(tranquillus.isLocationInside(location));
        ProjectileLaunchEvent event = new ProjectileLaunchEvent(testWorld.spawnArrow(location, location.toVector(), 1.0f, 0.0f));
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "ProjectileLaunchEvent not cancelled inside tranquillus area");

        // projectile launched outside spell area is allowed
        assertFalse(tranquillus.isLocationInside(outsideLocation));
        ProjectileLaunchEvent outsideEvent = new ProjectileLaunchEvent(testWorld.spawnArrow(outsideLocation, outsideLocation.toVector(), 1.0f, 0.0f));
        mockServer.getPluginManager().callEvent(outsideEvent);
        mockServer.getScheduler().performTicks(5);
        assertFalse(outsideEvent.isCancelled(), "ProjectileLaunchEvent cancelled outside tranquillus area");
    }
}