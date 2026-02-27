package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.ALOHOMORA;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ALOHOMORA spell functionality.
 *
 * <p>Tests the unlocking charm including:</p>
 * <ul>
 * <li>Spell failure when no COLLOPORTUS spells are at target location</li>
 * <li>Spell has no effect on non-COLLOPORTUS stationary spells</li>
 * <li>Successfully removes COLLOPORTUS stationary spells at target location</li>
 * </ul>
 *
 * @author Azami7
 */
public class AlohomoraTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ALOHOMORA;
    }

    @Override @Test
    void spellConstructionTest() {
    }

    /**
     * Test ALOHOMORA spell behavior for COLLOPORTUS removal.
     *
     * <p>Verifies that the spell sends a failure message when no COLLOPORTUS spells are found, leaves
     * other stationary spells unharmed, and successfully kills COLLOPORTUS spells at the target location.</p>
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().setType(Material.OAK_DOOR);

        // alohomora returns failure message when no spells found at target
        ALOHOMORA alohomora = (ALOHOMORA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(alohomora.isKilled(), "Alohomora did not hit target");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        assertEquals(alohomora.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");

        // alohomora has no effect on other stationary spells
        MUFFLIATO muffliato = new MUFFLIATO(testPlugin, caster.getUniqueId(), targetLocation, 5, 200);
        Ollivanders2API.getStationarySpells().addStationarySpell(muffliato);
        mockServer.getScheduler().performTicks(20);
        alohomora = (ALOHOMORA)castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(alohomora.isKilled());
        assertFalse(muffliato.isKilled(), "muffliato was killed");

        // alohomora kills colloportus spell at location
        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, caster.getUniqueId(), targetLocation);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        mockServer.getScheduler().performTicks(20);
        alohomora = (ALOHOMORA)castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(alohomora.isKilled());
        assertTrue(colloportus.isKilled(), "colloportus not killed");
        assertFalse(muffliato.isKilled(), "muffliato was killed");
    }

    /**
     * Alohomora has no revert actions
     */
    @Override @Test
    void revertTest() {

    }
}
