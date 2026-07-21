package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS}, the vanishing cabinet charm.
 *
 * @author Azami7
 */
public class HarmoniaNecterePassusTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.HARMONIA_NECTERE_PASSUS;
    }

    /**
     * Cover the success path and every failure gate: cabinets are created when both signs are configured, and no
     * stationary spell is created when a cabinet already exists there, the target is not a sign, the sign lacks valid
     * coordinates, or the sign points to its own location.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world1");
        Location location1 = new Location(testWorld, 200, 40, 100);
        Location location2 = new Location(testWorld, 220, 40, 100);
        Location castLocation = new Location(testWorld, 200, 39, 102);
        PlayerMock caster = mockServer.addPlayer();

        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location1, location2);
        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location2, location1);

        // cast the spell
        HARMONIA_NECTERE_PASSUS harmonia = (HARMONIA_NECTERE_PASSUS) castSpell(caster, castLocation, location1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmonia.hasHitBlock(), "harmonia did not hit target");

        // verify the stationary spells were successfully cast
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location1, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "harmonia spell killed");
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location2, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "twin spell killed");

        // spell fails if there is already a harmonia stationary spell
        harmonia = (HARMONIA_NECTERE_PASSUS) castSpell(caster, castLocation, location1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmonia.hasHitBlock());
        assertEquals(1, Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location1).size(), "harmonia created a stationary spell when there was already a spell present");

        // clean spells for next tests
        for (O2StationarySpell stationary : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            stationary.kill();
        }
        mockServer.getScheduler().performTicks(5);

        // spell fails if there is not a sign in the location
        location1.getBlock().setType(Material.DANDELION);
        harmonia = (HARMONIA_NECTERE_PASSUS) castSpell(caster, castLocation, location1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmonia.hasHitBlock());
        assertFalse(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location1, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "harmonia stationary spell was created when there was no sign");

        // spell fails if there is a sign, but it does not have the to location
        location1.getBlock().setType(Material.ACACIA_SIGN);
        harmonia = (HARMONIA_NECTERE_PASSUS) castSpell(caster, castLocation, location1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmonia.hasHitBlock());
        assertFalse(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location1, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "harmonia stationary spell was created when the sign had no text");

        // spell fails in the to and from location are the same
        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location1, location1);
        harmonia = (HARMONIA_NECTERE_PASSUS) castSpell(caster, castLocation, location1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmonia.hasHitBlock());
        assertFalse(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location1, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "harmonia stationary spell was created when to and from locations were the same");
    }

    /**
     * Harmonia has no revert actions
     */
    @Override @Test
    void revertTest() {

    }
}
