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
 * Test suite for the PROTEGO effect.
 *
 * <p>PROTEGO is an active projectile-tracking spell shield charm that blocks both physical projectiles
 * (arrows, snowballs, eggs, potions) and projectile spells. Unlike passive shields, PROTEGO actively
 * tracks projectiles within a 120-block range and removes them when they cross the shield boundary.
 * This test validates projectile tracking, removal mechanics, spell blocking, and event handling.</p>
 */
public class ProtegoTest extends ShieldSpellEffectTestSuper {
    /**
     * Create a PROTEGO effect for testing.
     *
     * <p>Instantiates a new PROTEGO effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new PROTEGO effect targeting the specified player
     */
    @Override
    PROTEGO createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new PROTEGO(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test that PROTEGO removes tracked projectiles when they enter the shield radius.
     *
     * <p>Validates that:</p>
     * <ul>
     * <li>Tracked projectiles outside the shield radius are not removed</li>
     * <li>Tracked projectiles that enter the shield radius are removed</li>
     * <li>Dead projectiles are cleaned up from the tracking list</li>
     * </ul>
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

    @Override
    void eventHandlerTests() {
        super.eventHandlerTests();

        doOnProjectileLaunchEventTest();
        doOnProjectileHitEvent();
    }

    /**
     * Test that projectiles launched within 120 blocks are tracked.
     *
     * <p>Validates that:</p>
     * <ul>
     * <li>Supported projectiles (arrows, snowballs, eggs, splash/lingering potions) within 120 blocks
     *     are added to the tracking list</li>
     * <li>Projectiles beyond 120 blocks are NOT tracked</li>
     * </ul>
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
     * Test that projectile hits to the shielded player are cancelled.
     *
     * <p>This is a placeholder for additional projectile hit testing.</p>
     */
    void doOnProjectileHitEvent() {
        Player target = mockServer.addPlayer();
        addEffect(target, 100, false);

        ProjectileHitEvent event = new ProjectileHitEvent(spawnAndLaunchProjectile(Snowball.class, target.getLocation()), target);
        mockServer.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled(), "ProjectileHitEvent not canceled");
    }

    /**
     * Test PROTEGO effect cleanup.
     *
     * <p>PROTEGO has no persistent state to clean up when removed.</p>
     */
    @Override
    void doRemoveTest() {
    }

    /**
     * Helper method to spawn a projectile and fire a launch event.
     *
     * <p>Spawns a projectile at the given location and fires a ProjectileLaunchEvent for it.
     * Specific to PROTEGO's projectile tracking testing needs.</p>
     *
     * @param projectileClass the class of projectile to spawn (e.g., Arrow.class)
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
