package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class StationarySpellTest extends O2SpellTestSuper {
    abstract O2StationarySpellType getStationarySpellType();

    @Override
    @Test
    void spellConstructionTest() {
    }

    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        StationarySpell stationarySpell = (StationarySpell) castSpell(caster, location, targetLocation);

        Location stationarySpellLocation;
        if (stationarySpell.isNoProjectile())
            stationarySpellLocation = location;
        else
            stationarySpellLocation = targetLocation;
        targetLocationSetup(stationarySpellLocation); // set up whatever blocks, entities, etc. need to be in place at the spell location

        // spell is killed after 1 tick if it is noProjectile
        mockServer.getScheduler().performTicks(1);
        if (!stationarySpell.isNoProjectile())
            assertFalse(stationarySpell.isKilled(), "Spell kill after 1 tick when it is not noProjectile");
        else
            assertTrue(stationarySpell.isKilled(), "Spell not killed after 1 tick when it is noProjectile");

        // spell is killed after hitting target if it is not noProjectile
        mockServer.getScheduler().performTicks(20);
        if (!stationarySpell.isNoProjectile()) {
            assertTrue(stationarySpell.hasHitTarget());
            assertTrue(stationarySpell.isKilled(), "Spell not killed after hitting target");
        }

        // radius and duration calculated
        assertTrue(stationarySpell.getDuration() >= stationarySpell.getMinDuration(), "duration < min duration");
        assertTrue(stationarySpell.getDuration() <= stationarySpell.getMaxDuration(), "duration > max duration");
        assertTrue(stationarySpell.getRadius() >= stationarySpell.getMinRadius(), "radius < min radius");
        assertTrue(stationarySpell.getRadius() <= stationarySpell.getMaxRadius(), "radius > max radius");

        // stationary spell was created
        List<O2StationarySpell> o2StationarySpells = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(stationarySpellLocation, getStationarySpellType());
        assertFalse(o2StationarySpells.isEmpty(), "Did not find " + getStationarySpellType().getSpellName() + " at location");

        // stationary spell radius correctly set
        O2StationarySpell o2StationarySpell = o2StationarySpells.getFirst();
        assertEquals(stationarySpell.getRadius(), o2StationarySpell.getRadius(), "stationary spell radius unexpected");
        if (!o2StationarySpell.isPermanent()) {
            if (stationarySpell.isNoProjectile())
                assertEquals(stationarySpell.getDuration() - 21, o2StationarySpell.getDuration(), "stationary spell duration not expected - noProjectile");
            else { // duration depends on when target was exactly hit, would be within 20 ticks of stationarySpell.getDuration()
                assertTrue(o2StationarySpell.getDuration() > (stationarySpell.getDuration() - 20), "stationary spell duration not expected");
                assertTrue(o2StationarySpell.getDuration() < (stationarySpell.getDuration()), "stationary spell duration not expected");
            }
        }
    }

    abstract void targetLocationSetup(Location location);

    @Override
    @Test
    void revertTest() {}
}
