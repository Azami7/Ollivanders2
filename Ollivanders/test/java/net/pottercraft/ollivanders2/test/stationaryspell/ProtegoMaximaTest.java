package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.FIENDFYRE;
import net.pottercraft.ollivanders2.spell.MELOFORS;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link PROTEGO_MAXIMA} stationary spell.
 *
 * <p>Tests the protego maxima comprehensive shield spell, which provides multiple layers of protection by:
 * <ul>
 *   <li>Tracking and killing projectiles that enter the protected area</li>
 *   <li>Preventing living entities outside the area from targeting players or passive mobs inside</li>
 *   <li>Blocking lower-level spell projectiles from crossing into the protected area</li>
 *   <li>Allowing all spells to exit the protected area from inside</li>
 *   <li>Preventing projectiles from hitting players or non-hostile mobs inside the area</li>
 *   <li>Allowing projectiles to hit hostile mobs inside the area</li>
 * </ul>
 * Inherits common spell tests from {@link O2StationarySpellTest} and provides spell-specific
 * factory methods for test setup.</p>
 *
 * @author Test Author
 */
public class ProtegoMaximaTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#PROTEGO_MAXIMA}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_MAXIMA;
    }

    /**
     * Creates a PROTEGO_MAXIMA spell instance for testing.
     *
     * <p>Constructs a new protego maxima spell at the specified location with the minimum radius and duration values.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new PROTEGO_MAXIMA spell instance (not null)
     */
    @Override
    PROTEGO_MAXIMA createStationarySpell(Player caster, Location location) {
        return new PROTEGO_MAXIMA(testPlugin, caster.getUniqueId(), location, PROTEGO_MAXIMA.minRadiusConfig, PROTEGO_MAXIMA.minDurationConfig);
    }

    /**
     * Tests upkeep behavior and projectile tracking.
     *
     * <p>Verifies that projectiles launched from outside the spell area are tracked and killed
     * when they enter the protected area.</p>
     */
    @Override
    @Test
    void upkeepTest() {
        // age() is tested by ageAndKillTest(), this test will handle the projectile logic
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_MAXIMA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_MAXIMA protegoMaxima = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoMaxima);
        mockServer.getScheduler().performTicks(20);

        // tracked projectiles are killed when they hit the spell area
        Entity arrow = launchProjectileEvent(outsideLocation, location);
        mockServer.getScheduler().performTicks(5);
        assertFalse(protegoMaxima.getTrackedProjectiles().isEmpty()); // make sure arrow is now being tracked
        mockServer.getScheduler().performTicks(20);// projectile travels at 2 blocks per tick
        assertTrue(protegoMaxima.isLocationInside(arrow.getLocation()), "arrow did not move to spell area");
        assertTrue(arrow.isDead(), "Projectile not killed when entering spell area");
    }

    /**
     * Tests entity targeting behavior at the spell boundary.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Players inside the spell area cannot be targeted by living entities outside</li>
     *   <li>Passive mobs inside the spell area cannot be targeted by entities outside</li>
     *   <li>Hostile mobs inside the spell area can still be targeted</li>
     *   <li>Entities outside the spell area are not affected by this protection</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnEntityTargetEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_MAXIMA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        PROTEGO_MAXIMA protegoMaxima = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoMaxima);
        mockServer.getScheduler().performTicks(20);

        // players inside spell area cannot be targeted by a living entity
        EntityTargetEvent event = new EntityTargetEvent(testWorld.spawn(outsideLocation, Skeleton.class), caster, EntityTargetEvent.TargetReason.UNKNOWN);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "player target event not cancelled inside spell area");

        // passive mobs in the spell area cannot be targeted
        event = new EntityTargetEvent(testWorld.spawn(outsideLocation, Skeleton.class), testWorld.spawn(location, Sheep.class), EntityTargetEvent.TargetReason.UNKNOWN);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "passive mob target event not cancelled inside spell area");

        // hostile mobs in the spell area can be targeted
        event = new EntityTargetEvent(testWorld.spawn(outsideLocation, Skeleton.class), testWorld.spawn(location, Skeleton.class), EntityTargetEvent.TargetReason.UNKNOWN);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "canceled hostile entity target event in spell area");

        // does not affect outside the spell area
        caster.setLocation(outsideLocation);
        event = new EntityTargetEvent(testWorld.spawn(outsideLocation, Skeleton.class), caster, EntityTargetEvent.TargetReason.UNKNOWN);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "player target event outside the spell area was cancelled");
    }

    /**
     * Tests spell projectile blocking behavior at the spell boundary.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Lower-level spell projectiles are blocked when crossing into the protected area</li>
     *   <li>Higher-level spell projectiles can cross into the protected area</li>
     *   <li>All spells can exit the protected area from inside</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnSpellProjectileMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_MAXIMA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(outsideLocation);

        PROTEGO_MAXIMA protegoMaxima = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoMaxima);
        mockServer.getScheduler().performTicks(20);

        // stop lower level spell coming in from outside the spell area
        OllivandersSpellProjectileMoveEvent event = new OllivandersSpellProjectileMoveEvent(player, new MELOFORS(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "Lower level spell projectile not killed when entering spell area");

        // allow higher-level spell coming in from outside spell area
        event = new OllivandersSpellProjectileMoveEvent(player, new FIENDFYRE(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "high level spell projectile killed when entering spell area");

        // allow all spells to go out of the spell area from inside
        event = new OllivandersSpellProjectileMoveEvent(player, new MELOFORS(testPlugin, caster, 1.0), location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "cancelled low level spell going outside of the spell area from inside");
    }

    /**
     * Tests projectile tracking when launched.
     *
     * <p>Verifies that when a projectile is launched toward the spell area, it is added to
     * the tracked projectiles list so it can be intercepted when it enters the protected area.</p>
     */
    @Test
    void doOnProjectileLaunchEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_MAXIMA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_MAXIMA protegoMaxima = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoMaxima);
        mockServer.getScheduler().performTicks(20);

        assertTrue(protegoMaxima.getTrackedProjectiles().isEmpty());

        launchProjectileEvent(outsideLocation, location);
        mockServer.getScheduler().performTicks(20);
        assertFalse(protegoMaxima.getTrackedProjectiles().isEmpty(), "did not add launched projectile to tracked projectiles");
    }

    /**
     * Helper method to launch a projectile and fire the ProjectileLaunchEvent.
     *
     * <p>Spawns an arrow at the specified location, calculates its direction toward the target location,
     * and fires the ProjectileLaunchEvent to allow spell event handlers to register tracking.</p>
     *
     * @param fromLocation the starting location for the projectile (not null)
     * @param toLocation   the target location for the projectile direction (not null)
     * @return the spawned arrow entity (not null)
     */
    Entity launchProjectileEvent(Location fromLocation, Location toLocation) {
        Vector direction = toLocation.toVector().subtract(fromLocation.toVector());
        Entity arrow = toLocation.getWorld().spawnArrow(fromLocation, direction, 2.0f, 0.0f);

        ProjectileLaunchEvent event = new ProjectileLaunchEvent(arrow);
        mockServer.getPluginManager().callEvent(event);

        return arrow;
    }

    /**
     * Tests projectile hit protection inside the spell area.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Projectiles hitting players inside the spell area are cancelled</li>
     *   <li>Projectiles hitting non-hostile mobs inside the spell area are cancelled</li>
     *   <li>Projectiles hitting hostile mobs inside the spell area are allowed</li>
     *   <li>Projectiles hitting players outside the spell area are not affected</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnProjectileHitEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_MAXIMA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        PROTEGO_MAXIMA protegoMaxima = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoMaxima);
        mockServer.getScheduler().performTicks(20);

        // prevent players in area being hit by projectiles
        ProjectileHitEvent event = new ProjectileHitEvent(testWorld.spawn(location, ShulkerBullet.class), caster, null);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "projectile hit event on player in spell area not cancelled");

        // prevent non-hostile mobs being hit by projectiles
        event = new ProjectileHitEvent(testWorld.spawn(location, ShulkerBullet.class), testWorld.spawn(location, Cow.class), null);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "projectile hit event on non-hostile mob in spell area not cancelled");

        // allow projectiles to hit hostile mobs
        event = new ProjectileHitEvent(testWorld.spawn(location, ShulkerBullet.class), testWorld.spawn(location, Zombie.class), null);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "projectile hit event on hostile mob in spell area cancelled");

        // no impact to players outside of spell area
        caster.setLocation(outsideLocation);
        event = new ProjectileHitEvent(testWorld.spawn(location, ShulkerBullet.class), caster, null);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "projectile hit event on player outside spell area cancelled");
    }
}
