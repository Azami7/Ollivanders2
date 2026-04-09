package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.spell.Galeati;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base test for {@link Galeati} helmet-replacement spells.
 *
 * <p>Provides shared test coverage for the helmet-placement mechanics that all Galeati
 * subclasses ({@link net.pottercraft.ollivanders2.spell.HERBIFORS},
 * {@link net.pottercraft.ollivanders2.spell.MELOFORS}) inherit. Subclasses override
 * {@link #getSpellType()} to specify which spell to test; the same test logic validates
 * both spell variants.
 *
 * <p>Tests cover:
 * <ul>
 * <li><strong>No target:</strong> Spell hits a block with no nearby players and is killed</li>
 * <li><strong>Bare-headed player:</strong> Spell places the correct helmet type on a player with no helmet</li>
 * <li><strong>Helmeted player:</strong> Spell drops the existing helmet as an item and replaces it</li>
 * </ul>
 *
 * @author Azami7
 */
abstract public class GaleatiTest extends O2SpellTestSuper {
    /**
     * Tests the full progression of Galeati helmet-replacement scenarios.
     *
     * <p>Sub-tests run in order against the same world and target player:
     * <ul>
     * <li>Spell cast with no nearby player — killed on block hit, no side effects</li>
     * <li>Spell cast at a bare-headed player — helmet of {@link Galeati#getHelmetType()} placed</li>
     * <li>Spell cast at a player wearing an iron helmet — existing helmet dropped at eye location,
     *     new helmet of {@link Galeati#getHelmetType()} placed</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        Galeati galeati = (Galeati) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(galeati.isKilled(), "spell not killed when it hit a block");

        PlayerMock targetPlayer = mockServer.addPlayer();
        targetPlayer.setLocation(targetLocation);
        EntityEquipment playerEquipment = targetPlayer.getEquipment();
        assertNotNull(playerEquipment, "target player has no equipment");
        assertNull(playerEquipment.getHelmet(), "Player should start with no helmet");
        galeati = (Galeati) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(galeati.isKilled(), "spell not killed when it hit an entity");
        playerEquipment = targetPlayer.getEquipment();
        assertNotNull(playerEquipment, "target player has no equipment");
        ItemStack helmet = playerEquipment.getHelmet();
        assertNotNull(helmet, "target player is not wearing a helmet");
        assertEquals(galeati.getHelmetType(), helmet.getType(), "helmet is not expected type");

        playerEquipment.setHelmet(new ItemStack(Material.IRON_HELMET));
        galeati = (Galeati) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        playerEquipment = targetPlayer.getEquipment();
        assertNotNull(playerEquipment, "target player has no equipment");
        helmet = playerEquipment.getHelmet();
        assertNotNull(helmet, "target player is not wearing a helmet");
        assertEquals(galeati.getHelmetType(), helmet.getType(), "helmet is not expected type");
        Item droppedHelmet = EntityCommon.getItemAtLocation(targetPlayer.getEyeLocation());
        assertNotNull(droppedHelmet, "player's original helmet was not dropped at the player location");
        assertEquals(Material.IRON_HELMET, droppedHelmet.getItemStack().getType(), "dropped original helmet not expected type");
    }

    /**
     * No-op revert test — Galeati spells have no revert actions to undo.
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
