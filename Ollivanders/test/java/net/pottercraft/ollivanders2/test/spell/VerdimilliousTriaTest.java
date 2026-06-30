package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.GEMINO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.VERDIMILLIOUS_DUO;
import net.pottercraft.ollivanders2.spell.VERDIMILLIOUS_TRIA;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the VERDIMILLIOUS_TRIA spell, the strongest green-sparks variant.
 *
 * <p>VERDIMILLIOUS_TRIA inherits the damage and curse-detection behavior of {@link VERDIMILLIOUS_DUO}; the
 * shared damage-and-sound path is covered by the inherited {@link SparksTest#doCheckEffectTest()}. What sets
 * TRIA apart is its higher magic level (NEWT vs the DUO's OWL), which lets it reveal more powerful curses. This
 * test covers that distinguishing behavior.</p>
 *
 * @author Azami7
 */
public class VerdimilliousTriaTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VERDIMILLIOUS_TRIA;
    }

    /**
     * Tests that TRIA reveals a curse too powerful for the DUO variant to detect.
     *
     * <p>Curse detection compares the curse's level to the casting spell's level: a curse is revealed when its
     * level ordinal is at most one above the spell's level. GEMINO applies an EXPERT-level curse, which the
     * OWL-level DUO cannot detect (see VerdimilliousDuoTest), but the NEWT-level TRIA can. This verifies the
     * spell makes the GEMINO-cursed item glow, confirming TRIA's higher detection reach.</p>
     */
    @Test
    void higherLevelCurseDetectionTest() {
        World testWorld = mockServer.addSimpleWorld("VerdimilliousTria_expertCurse");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // floor under the target so the cursed item does not fall out of the spell's path
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        testWorld.dropItem(targetLocation, new ItemStack(Material.DIAMOND_SWORD, 1));

        // GEMINO applies an EXPERT-level curse - undetectable by the OWL-level DUO, detectable by the NEWT-level TRIA
        GEMINO gemino = (GEMINO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 20, O2SpellType.GEMINO);
        mockServer.getScheduler().performTicks(20);
        Item cursedItem = EntityCommon.getItemAtLocation(gemino.getLocation());
        assertNotNull(cursedItem);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isCursed(cursedItem), "GEMINO did not curse the item");

        caster.setLocation(location);
        VERDIMILLIOUS_TRIA verdimilliousTria = (VERDIMILLIOUS_TRIA) castSpell(caster, location, cursedItem.getLocation());
        mockServer.getScheduler().performTicks(20);

        assertTrue(verdimilliousTria.isKilled(), "spell did not hit the cursed item");
        assertTrue(cursedItem.isGlowing(), "TRIA did not reveal the EXPERT-level curse");
    }
}