package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.MULTICORFORS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link MULTICORFORS}, the Multicorfors color-changing spell.
 *
 * <p>Tests cover the entity-scanning projectile model and the leather-armor recoloring behavior:
 * <ul>
 * <li><strong>No target:</strong> Spell hits a block with no entities nearby — killed, no failure message</li>
 * <li><strong>Non-leather entity:</strong> Entity without leather armor (cow) — killed, failure message sent</li>
 * <li><strong>Empty armor stand:</strong> Armor stand with no equipment — killed, failure message sent</li>
 * <li><strong>Iron armor:</strong> Non-leather armor is not modified, failure message sent</li>
 * <li><strong>Single leather piece:</strong> Leather chestplate is recolored to the spell's random color</li>
 * <li><strong>Multiple leather pieces:</strong> All leather pieces receive the same random color</li>
 * </ul>
 *
 * @author Azami7
 */
public class MulticorforsTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MULTICORFORS;
    }

    /**
     * Tests the full progression of Multicorfors targeting scenarios against various entity
     * and equipment configurations.
     *
     * <p>Sub-tests are ordered so that entities from earlier scenarios remain in the world,
     * verifying the spell's iteration logic correctly skips entities without leather armor
     * and continues searching. Each sub-test clears the caster's message queue after
     * assertions to avoid stale messages leaking into the next sub-test.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        MULTICORFORS multicorfors = (MULTICORFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(multicorfors.isKilled(), "spell not killed when it hit a block");
        String message = caster.nextMessage();
        assertNull(message, "caster received failure message when spell did not hit an entity and fail to change armor color");
        TestCommon.clearMessageQueue(caster);

        testWorld.spawnEntity(targetLocation, EntityType.COW);
        multicorfors = (MULTICORFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(multicorfors.isKilled(), "spell not killed when it hit an entity");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when armor stand has no equipment");
        TestCommon.clearMessageQueue(caster);

        ArmorStand armorStand = (ArmorStand) testWorld.spawnEntity(targetLocation, EntityType.ARMOR_STAND);
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when armor stand has no armor");
        TestCommon.clearMessageQueue(caster);

        armorStand.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        multicorfors = (MULTICORFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(multicorfors.isKilled(), "spell not killed when it hit an entity");
        assertEquals(Material.IRON_CHESTPLATE, armorStand.getEquipment().getChestplate().getType(), "armor stand equipment unexpectedly changed");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        TestCommon.clearMessageQueue(caster);

        armorStand.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        multicorfors = (MULTICORFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        Color color = multicorfors.getColor();
        ItemStack newChestplate = armorStand.getEquipment().getChestplate();
        assertNotNull(newChestplate, "armor stand chestplate removed");
        assertEquals(Material.LEATHER_CHESTPLATE, newChestplate.getType(), "armor stand chestplate unexpected type");
        LeatherArmorMeta newChestplateMeta = (LeatherArmorMeta) newChestplate.getItemMeta();
        assertEquals(color, newChestplateMeta.getColor(), "armor stand chestplate color unexpected");

        armorStand.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        armorStand.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        multicorfors = (MULTICORFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        color = multicorfors.getColor();
        newChestplate = armorStand.getEquipment().getChestplate();
        ItemStack newBoots = armorStand.getEquipment().getBoots();
        newChestplateMeta = (LeatherArmorMeta) newChestplate.getItemMeta();
        assertEquals(color, newChestplateMeta.getColor(), "armor stand chestplate color unexpected");
        LeatherArmorMeta newBootMeta = (LeatherArmorMeta) newBoots.getItemMeta();
        assertEquals(newChestplateMeta.getColor(), newBootMeta.getColor(), "boots and chestplate are not the same color");
    }

    /**
     * No-op revert test — MULTICORFORS has no revert actions to undo.
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
