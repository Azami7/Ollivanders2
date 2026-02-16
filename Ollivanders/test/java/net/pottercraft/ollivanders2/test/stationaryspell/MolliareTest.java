package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.MOLLIARE;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link MOLLIARE} stationary spell.
 *
 * <p>Tests the molliare charm spell (Cushioning Charm), which prevents entities from taking fall
 * damage within the spell's protected area. Inherits common spell tests from
 * {@link O2StationarySpellTest} and provides spell-specific factory methods for test setup.</p>
 *
 * <p>The test verifies:
 * <ul>
 *   <li>Entities inside the spell area do not take fall damage</li>
 *   <li>Entities outside the spell area take normal fall damage</li>
 *   <li>Entities inside the spell area still take damage from other sources</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 */
public class MolliareTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#MOLLIARE}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.MOLLIARE;
    }

    /**
     * Creates a MOLLIARE spell instance for testing.
     *
     * <p>Constructs a new molliare spell at the specified location with the test's configured
     * radius and duration values (minimum values by default).</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new MOLLIARE spell instance (not null)
     */
    @Override
    MOLLIARE createStationarySpell(Player caster, Location location) {
        return new MOLLIARE(testPlugin, caster.getUniqueId(), location, MOLLIARE.minRadiusConfig, MOLLIARE.minDurationConfig);
    }

    /**
     * Tests molliare upkeep behavior (skipped - covered by base class tests).
     *
     * <p>The molliare spell's upkeep method only performs aging, which is already tested
     * comprehensively by the inherited ageAndKillTest() from the base test class.</p>
     */
    @Override
    @Test
    void upkeepTest() {
        // upkeep only does age, which is covered by ageAndKillTest()
    }

    /**
     * Tests fall damage negation and selective damage blocking behavior.
     *
     * <p>Verifies that the spell properly handles entity damage events:
     * <ul>
     *   <li>Entities inside the spell area do not take fall damage (event is cancelled)</li>
     *   <li>Entities outside the spell area take normal fall damage (event is not cancelled)</li>
     *   <li>Entities inside the spell area still take damage from other sources like fire</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnEntityDamageEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX() + MOLLIARE.maxRadiusConfig + 1, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        MOLLIARE molliare = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(molliare);

        // player inside spell area doesn't take fall damage
        caster.setLocation(location);
        assertTrue(molliare.isLocationInside(caster.getLocation()));
        DamageSource damageSource = DamageSource.builder(DamageType.FALL)
                .withDamageLocation(caster.getLocation())  // location of the fire block
                .build();
        EntityDamageEvent event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FALL, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "Fall damage event not cancelled inside molliare");

        // player outside spell area takes fall damage
        caster.setLocation(outsideLocation);
        assertFalse(molliare.isLocationInside(caster.getLocation()));
        event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FALL, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "fall damage event outside molliare was cancelled");

        // player inside spell area takes damage from other sources
        caster.setLocation(location);
        assertTrue(molliare.isLocationInside(caster.getLocation()));
        damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(caster.getLocation())  // location of the fire block
                .build();
        event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "Non-fall damage event cancelled inside molliare");
    }
}
