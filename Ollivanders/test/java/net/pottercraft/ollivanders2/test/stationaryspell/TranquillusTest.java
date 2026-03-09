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
 * Unit tests for the {@link TRANQUILLUS} stationary spell.
 *
 * <p>TRANQUILLUS creates a zone of tranquility that prevents hostile mob targeting and projectile
 * launches within its radius. Tests verify that the spell correctly cancels entity target events
 * and projectile launch events for entities inside the spell area, while allowing normal behavior
 * for entities outside the area.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Duration and radius management (inherited from O2StationarySpellTest)</li>
 * <li>Spell aging and expiration (inherited)</li>
 * <li>Location-based area detection (inherited)</li>
 * <li>Entity target event cancellation inside spell area</li>
 * <li>Entity target event allowed outside spell area</li>
 * <li>Projectile launch event cancellation inside spell area</li>
 * <li>Projectile launch event allowed outside spell area</li>
 * </ul>
 *
 * @see TRANQUILLUS for the spell implementation
 * @see O2StationarySpellTest for inherited test framework
 */
public class TranquillusTest extends O2StationarySpellTest {
    /**
     * Get the spell type being tested.
     *
     * @return {@link O2StationarySpellType#TRANQUILLUS}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.TRANQUILLUS;
    }

    /**
     * Creates a TRANQUILLUS spell instance for testing.
     *
     * <p>Constructs a new tranquillus spell at the specified location with minimum radius
     * and duration values.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new TRANQUILLUS spell instance (not null)
     */
    @Override
    TRANQUILLUS createStationarySpell(Player caster, Location location) {
        return new TRANQUILLUS(testPlugin, caster.getUniqueId(), location, TRANQUILLUS.minRadiusConfig, TRANQUILLUS.minDurationConfig);
    }

    /**
     * Tests tranquillus upkeep behavior.
     *
     * <p>The tranquillus spell's upkeep method only performs aging, which is already tested
     * comprehensively by the inherited ageAndKillTest() from the base test class.</p>
     */
    @Override
    @Test
    void upkeepTest() {
        // upkeep only does age, which is covered by ageAndKillTest()
    }

    /**
     * Test that entity targeting is cancelled inside the spell area and allowed outside.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Entities inside the spell area cannot target other entities</li>
     * <li>Entities outside the spell area can target normally</li>
     * </ul></p>
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
     * Test that projectile launches are cancelled inside the spell area and allowed outside.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Projectiles launched from inside the spell area are cancelled</li>
     * <li>Projectiles launched from outside the spell area are allowed</li>
     * </ul></p>
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