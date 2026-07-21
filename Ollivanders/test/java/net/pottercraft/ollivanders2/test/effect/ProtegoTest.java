package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.PROTEGO;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PROTEGO}, an active shield charm that tracks projectiles within 120 blocks and removes them
 * when they cross the shield boundary.
 *
 * @see ShieldSpellEffectTestSuper
 */
public class ProtegoTest extends ShieldSpellEffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    PROTEGO createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new PROTEGO(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Tracked projectiles are removed once inside the shield radius, left alone while outside it, and pruned from the
     * tracking list once dead.
     */
    @Override
    void checkEffectTest() {
        Object[] setup = setupShieldedPlayerAtLocation(0, 4, 0);
        PROTEGO effect = (PROTEGO) setup[1];

        // Test 1: Tracked projectile outside shield should not be removed
        Snowball outsideSnowball = spawnAndLaunchProjectile(Snowball.class, new Location(testWorld, 100, 4, 0));
        assertTrue(effect.isProjectileTracked(outsideSnowball), "Snowball not tracked initially");

        // Execute checkEffect tick
        effect.checkEffect();

        // Snowball should still exist (not removed)
        assertFalse(outsideSnowball.isDead(), "Tracked projectile removed while outside shield radius");
        assertTrue(effect.isProjectileTracked(outsideSnowball), "Tracked projectile removed from tracking while outside shield");

        // Test 2: Tracked projectile inside shield should be removed
        Snowball insideSnowball = spawnAndLaunchProjectile(Snowball.class, new Location(testWorld, 2, 4, 0));
        assertTrue(effect.isProjectileTracked(insideSnowball), "Snowball not tracked initially");

        // Execute checkEffect tick
        effect.checkEffect();

        // Snowball should be removed (dead)
        assertTrue(insideSnowball.isDead(), "Tracked projectile not removed when entering shield radius");

        // Test 3: Dead projectiles are cleaned from tracking list
        Arrow deadArrow = spawnAndLaunchProjectile(Arrow.class, new Location(testWorld, 50, 4, 0));
        assertTrue(effect.isProjectileTracked(deadArrow), "Arrow not tracked initially");

        // Manually kill the arrow
        deadArrow.remove();

        // Execute checkEffect to clean up dead projectiles
        effect.checkEffect();

        // Arrow should be removed from tracking list
        assertFalse(effect.isProjectileTracked(deadArrow), "Dead projectile not cleaned from tracking list");
    }

    /**
     * Run the inherited shield tests plus PROTEGO's projectile launch tracking and hit cancellation checks.
     */
    @Override
    void eventHandlerTests() {
        super.eventHandlerTests();

        doOnProjectileLaunchEventTest();
        doOnProjectileHitEvent();
    }

    /**
     * Supported projectiles launched within 120 blocks are added to the tracking list; those beyond 120 blocks are not.
     */
    void doOnProjectileLaunchEventTest() {
        Object[] setup = setupShieldedPlayerAtLocation(0, 4, 0);
        PROTEGO effect = (PROTEGO) setup[1];

        // Test 1: Arrow within 120 blocks should be tracked
        Arrow arrow = spawnAndLaunchProjectile(Arrow.class, new Location(testWorld, 50, 4, 0));
        assertTrue(effect.isProjectileTracked(arrow), "Arrow within 120 blocks not tracked by PROTEGO");

        // Test 2: Arrow beyond 120 blocks should NOT be tracked
        Arrow farArrow = spawnAndLaunchProjectile(Arrow.class, new Location(testWorld, 150, 4, 0));
        assertFalse(effect.isProjectileTracked(farArrow), "Arrow beyond 120 blocks was tracked by PROTEGO");

        // Test 3: Snowball within 120 blocks should be tracked
        Snowball snowball = spawnAndLaunchProjectile(Snowball.class, new Location(testWorld, 30, 4, 0));
        assertTrue(effect.isProjectileTracked(snowball), "Snowball within 120 blocks not tracked by PROTEGO");
    }

    /**
     * A projectile hit on the shielded player is cancelled.
     */
    void doOnProjectileHitEvent() {
        Player target = mockServer.addPlayer();
        addEffect(target, 100, false);

        ProjectileHitEvent event = new ProjectileHitEvent(spawnAndLaunchProjectile(Snowball.class, target.getLocation()), target);
        mockServer.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled(), "ProjectileHitEvent not canceled");
    }

    /**
     * PROTEGO has no persistent state to clean up when removed.
     */
    @Override
    void doRemoveTest() {
    }

    /**
     * Spawn a projectile at the given location and fire a {@link ProjectileLaunchEvent} for it.
     *
     * @param projectileClass the class of projectile to spawn (e.g. Arrow.class)
     * @param location        the location to spawn the projectile
     * @return the spawned projectile
     */
    private <T extends Projectile> T spawnAndLaunchProjectile(Class<T> projectileClass, Location location) {
        T projectile = testWorld.spawn(location, projectileClass);
        ProjectileLaunchEvent event = new ProjectileLaunchEvent(projectile);
        mockServer.getPluginManager().callEvent(event);
        return projectile;
    }
}
