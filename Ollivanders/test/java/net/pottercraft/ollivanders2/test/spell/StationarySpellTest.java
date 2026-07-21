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

/**
 * Base test class for {@link StationarySpell} spells, verifying the spell ends correctly and creates a stationary
 * spell with the right location, radius, and duration. Subclasses supply the stationary spell type and target setup.
 *
 * @author Azami7
 * @see O2SpellTestSuper
 * @see StationarySpell
 */
abstract public class StationarySpellTest extends O2SpellTestSuper {
    /**
     * @return the stationary spell type this test creates
     */
    abstract O2StationarySpellType getStationarySpellType();

    /**
     * Verify the spell ends (immediately for noProjectile, otherwise on hitting its target) and creates a stationary
     * spell at the right location with radius and duration within bounds.
     */
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
            assertTrue(stationarySpell.hasHitBlock());
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

    /**
     * Hook for subclasses to place any blocks or entities needed at the spell's target location before the effect is
     * verified.
     *
     * @param location the target location where the spell will take effect
     */
    abstract void targetLocationSetup(Location location);

    /**
     * No-op: stationary spell casters have no revert action of their own.
     */
    @Override
    @Test
    void revertTest() {
    }
}
