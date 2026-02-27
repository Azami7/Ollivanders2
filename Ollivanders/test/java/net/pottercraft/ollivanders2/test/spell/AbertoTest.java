package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.ABERTO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ABERTO spell functionality.
 *
 * <p>Tests the opening charm including:</p>
 * <ul>
 * <li>Spell failure when cast at non-door blocks</li>
 * <li>Successfully opening closed doors and trapdoors</li>
 * <li>Spell failure when target door is protected by COLLOPORTUS</li>
 * </ul>
 *
 * <p>Validates that doors transition from closed to open state when the spell is cast, and that
 * COLLOPORTUS stationary spells successfully block the opening spell.</p>
 *
 * @author Azami7
 */
@Isolated
public class AbertoTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ABERTO;
    }

    @Override @Test
    void spellConstructionTest() {
        // aberto has no spell-specific settings
    }

    /**
     * Test ABERTO spell behavior for door opening and locking protection.
     *
     * <p>Verifies that the spell fails gracefully when cast at non-door blocks, successfully
     * opens doors by setting their Openable state to true, and fails to open doors protected
     * by COLLOPORTUS stationary spells.</p>
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        Block doorBlock = targetLocation.getBlock();

        // spell fails when not cast at a door
        doorBlock.setType(Material.STONE);
        ABERTO aberto = (ABERTO)castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aberto.isKilled(), "spell not killed");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message");
        assertEquals(aberto.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");

        // spell opens the door when the target is a closed door/trapdoor/gate
        assertFalse(Ollivanders2Common.getDoors().isEmpty());
        doorBlock.setType(Ollivanders2Common.getDoors().getFirst());
        Openable door = (Openable)doorBlock.getBlockData();
        door.setOpen(false);
        doorBlock.setBlockData(door);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        door = (Openable)doorBlock.getBlockData();
        assertTrue(door.isOpen(), "door was not opened");

        // spell fails if a colloportus is present
        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, caster.getUniqueId(), targetLocation);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);
        door.setOpen(false);
        doorBlock.setBlockData(door);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        door = (Openable)doorBlock.getBlockData();
        assertFalse(door.isOpen(), "door was opened when colloportus present");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message");
    }

    /**
     * Aberto has no revert actions.
     */
    @Override @Test
    void revertTest() {

    }
}
