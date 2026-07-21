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
 * Base test class for {@link Galeati} helmet-replacement spells, covering the no-target, bare-headed, and
 * already-helmeted cases.
 *
 * @author Azami7
 */
abstract public class GaleatiTest extends O2SpellTestSuper {
    /**
     * Verify the spell is killed with no effect when it finds no player, places the correct helmet on a bare-headed
     * player, and replaces an existing helmet (dropping the old one) on a helmeted player.
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
