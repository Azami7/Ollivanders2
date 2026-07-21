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
 * Unit tests for {@link MOLLIARE}. Extends {@link O2StationarySpellTest} for the shared stationary-spell tests.
 *
 * @author Azami7
 */
public class MolliareTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.MOLLIARE;
    }

    @Override
    MOLLIARE createStationarySpell(Player caster, Location location) {
        return new MOLLIARE(testPlugin, caster.getUniqueId(), location, MOLLIARE.minRadiusConfig, MOLLIARE.minDurationConfig);
    }

    @Override
    @Test
    void upkeepTest() {
        // upkeep only does age, which is covered by ageAndKillTest()
    }

    /**
     * Fall damage is cancelled inside the spell area but not outside it, and non-fall damage inside is not cancelled.
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
                .withDamageLocation(caster.getLocation())
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
                .withDamageLocation(caster.getLocation())
                .build();
        event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "Non-fall damage event cancelled inside molliare");
    }
}
