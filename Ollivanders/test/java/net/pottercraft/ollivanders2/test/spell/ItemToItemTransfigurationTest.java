package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.ItemToItemTransfiguration;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link ItemToItemTransfiguration} spells, covering valid/invalid item targeting, canTransfigure
 * filtering, partial-stack remainders, and the permanent (no-revert) lifecycle. Subclasses supply the valid and
 * invalid item materials.
 *
 * @author Azami7
 */
abstract public class ItemToItemTransfigurationTest extends O2SpellTestSuper {
    /**
     * @return a material this spell can transfigure
     */
    abstract Material getValidItemType();

    /**
     * @return a material this spell cannot transfigure
     */
    abstract Material getInvalidItemType();

    /**
     * Verify the spell is killed without transfiguring an invalid item type, then transfigures a valid one.
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        Item original = testWorld.dropItem(targetLocation, new ItemStack(getInvalidItemType(), 1));

        ItemToItemTransfiguration itemToItemTransfiguration = (ItemToItemTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(itemToItemTransfiguration.isKilled(), "spell not killed when item was invalid type");
        assertFalse(itemToItemTransfiguration.isTransfigured(), "spell transfigured invalid item type");
        assertFalse(original.isDead(), "original item is dead when item was invalid type");
        assertEquals(1, EntityCommon.getEntitiesInRadius(targetLocation, 3).size(), "unexpected number of items at target location");
        original.remove();

        original = testWorld.dropItem(targetLocation, new ItemStack(getValidItemType(), 1));
        itemToItemTransfiguration = (ItemToItemTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(itemToItemTransfiguration.isTransfigured(), "spell did not transfigure valid item type");
        assertTrue(original.isDead(), "original item not removed");
        assertTrue(itemToItemTransfiguration.isKilled(), "spell not killed after transfiguration completed");
        assertEquals(1, EntityCommon.getEntitiesInRadius(targetLocation, 3).size(), "unexpected number of items at target location");
    }

    /**
     * Test canTransfigure with unbreakable materials, enchanted items, and invalid material types.
     */
    @Test
    void canTransfigureTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemToItemTransfiguration itemToItemTransfiguration = (ItemToItemTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        // blocked material
        Material originalType = Ollivanders2Common.getUnbreakableMaterials().getFirst();
        Item blockedItem = testWorld.dropItem(targetLocation, new ItemStack(originalType, 1));
        assertFalse(itemToItemTransfiguration.canTransfigure(blockedItem), "canTransfigure() returned true for blocked item type");

        // enchanted item
        if (itemToItemTransfiguration.doesTransfigureEnchanted()) {
            ItemStack broomstick = O2ItemType.BROOMSTICK.getItem(1);
            assertNotNull(broomstick);
            Item enchantedItem = testWorld.dropItem(targetLocation, broomstick);
            assertFalse(itemToItemTransfiguration.canTransfigure(enchantedItem), "canTransfigure() returned true for enchanted item type");
        }

        // transfiguration map
        Item invalidItem = testWorld.dropItem(targetLocation, new ItemStack(getInvalidItemType(), 1));
        assertFalse(itemToItemTransfiguration.canTransfigure(invalidItem), "canTransfigure() returned true for invalid item type");
    }

    /**
     * Test that partial stack transfiguration leaves the correct remainder on the original item
     * and that the total item count across all stacks is preserved.
     */
    @Test
    void remainderItemsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        int amount = 15;
        Item original = testWorld.dropItem(targetLocation, new ItemStack(getValidItemType(), amount));

        ItemToItemTransfiguration itemToItemTransfiguration = (ItemToItemTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(itemToItemTransfiguration.isTransfigured());
        assertFalse(original.isDead(), "original item was removed when there is leftover amount");
        int remainder = original.getItemStack().getAmount();
        assertEquals(5, remainder, "Unexpected remainder amount");
        List<Item> items = EntityCommon.getItemsInRadius(targetLocation, 2);
        int totalAmount = 0;
        for (Item item : items) {
            totalAmount = totalAmount + item.getItemStack().getAmount();
        }
        assertEquals(amount, totalAmount, "total amount left in item stacks does not equal original amount");
    }

    /**
     * We can't test this yet because there is no transfiguration spell that results in an Item entity type and is not permanent.
     */
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
    }

    /**
     * These are permanent spells so there is no revert action.
     */
    @Override
    @Test
    void revertTest() {

    }
}
