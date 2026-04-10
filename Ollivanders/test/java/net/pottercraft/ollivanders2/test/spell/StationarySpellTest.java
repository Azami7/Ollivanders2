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
 * Abstract base test class for stationary spell implementations.
 *
 * <p>Provides common test infrastructure for testing spells that create stationary effects
 * in the world (as opposed to instant-cast spells). Subclasses must implement the spell-specific
 * setup and behavior testing.</p>
 *
 * <p>Test Coverage:</p>
 *
 * <ul>
 * <li>doCheckEffectTest: Verifies that stationary spells are correctly created with proper radius and duration</li>
 * <li>targetLocationSetup: Hook for subclasses to set up the target location for spell casting</li>
 * </ul>
 *
 * @author Azami7
 * @see O2SpellTestSuper
 * @see StationarySpell
 */
abstract public class StationarySpellTest extends O2SpellTestSuper {
    /**
     * Gets the spell type for this test.
     *
     * @return the stationary spell type being tested
     */
    abstract O2StationarySpellType getStationarySpellType();

    /**
     * Tests the core effects of stationary spell creation and configuration.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The spell is killed appropriately (immediately for noProjectile, after hitting target otherwise)</li>
     * <li>Radius and duration are calculated and clamped to min/max bounds</li>
     * <li>A stationary spell instance is created at the correct location</li>
     * <li>The stationary spell has the correct radius and duration values</li>
     * </ul>
     * </p>
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
     * Hook for subclasses to set up the target location before spell effects are verified.
     *
     * <p>Subclasses should override this to place any necessary blocks, entities, or other setup
     * required for testing spell effects at the target location.</p>
     *
     * @param location the target location where the spell will take effect
     */
    abstract void targetLocationSetup(Location location);

    /**
     * Revert test (overridden with empty implementation).
     */
    @Override
    @Test
    void revertTest() {
    }
}
