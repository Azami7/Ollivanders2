package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.ItemEnchant;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link ItemEnchant} spells, covering projectile and held-item enchantment, stack splitting,
 * magnitude scaling, and item-type validation. Subclasses supply the valid/invalid item types and implement the
 * spell-specific {@link #createEnchantmentArgsTest()} and {@link #alterItemTest()}.
 *
 * @see ItemEnchant
 * @see O2SpellTestSuper
 */
abstract class ItemEnchantTest extends O2SpellTestSuper {
    /**
     * @return a material this enchantment spell can target
     */
    @NotNull
    abstract Material getValidItemType();

    /**
     * @return a material this enchantment spell cannot target, or null to skip invalid-type testing (e.g. when the
     *         spell accepts all materials or filters by O2ItemType instead)
     */
    @Nullable
    abstract Material getInvalidItemType();

    /**
     * Create a valid item stack this spell can target — from the spell's O2ItemType allow list if it has one,
     * otherwise of {@link #getValidItemType()}.
     *
     * @param itemEnchant the spell being tested
     * @param amount      the number of items in the stack
     * @return a valid ItemStack that can be enchanted
     */
    @NotNull
    ItemStack createValidItem(@NotNull ItemEnchant itemEnchant, int amount) {
        ItemStack itemStack = null;

        // check if this spell supports O2Items first
        if (!itemEnchant.getO2ItemTypeAllowList().isEmpty())
            itemStack = itemEnchant.getO2ItemTypeAllowList().getFirst().getItem(amount);

        if (itemStack == null)
            itemStack = new ItemStack(getValidItemType(), amount);

        return itemStack;
    }

    /**
     * Create an invalid item stack this spell cannot target — of {@link #getInvalidItemType()}, or a BARRIER when the
     * spell has no invalid material type.
     *
     * @return an invalid ItemStack that cannot be enchanted
     */
    @NotNull
    ItemStack createInvalidItem() {
        ItemStack itemStack = null;

        Material invalidMaterial = getInvalidItemType();
        if (invalidMaterial == null)
            itemStack = new ItemStack(Material.BARRIER, 1);
        else
            itemStack = new ItemStack(invalidMaterial, 1);

        return itemStack;
    }

    /**
     * Verify a projectile spell enchants a dropped item at its endpoint: the original is removed and a correctly
     * enchanted item is dropped in its place.
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        if (!itemEnchant.isNoProjectile()) {
            Item item = testWorld.dropItem(targetLocation, createValidItem(itemEnchant, 1));
            assertTrue(itemEnchant.canBeEnchanted(item));
            mockServer.getScheduler().performTicks(20);

            assertTrue(itemEnchant.isKilled(), "spell was not killed when target hit");
            assertTrue(item.isDead(), "Original item not removed");
            Item enchantedItem = EntityCommon.getItemAtLocation(itemEnchant.getLocation());
            assertNotNull(enchantedItem, "Enchanted item not dropped");
            assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(enchantedItem), "valid item type was not enchanted");
            assertEquals(itemEnchant.getEnchantmentType(), Ollivanders2API.getItems().enchantedItems.getEnchantmentType(enchantedItem.getItemStack()), "enchantment type not expected");
        }
    }

    /**
     * Verify a held-item spell does nothing when the off-hand is empty and enchants a valid off-hand item otherwise.
     */
    @Test
    void affectsHeldItemTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        if (itemEnchant.isNoProjectile()) {
            // test player holding nothing
            mockServer.getScheduler().performTicks(2);
            assertTrue(itemEnchant.isKilled(), "spell not killed immediately");
            Item enchantedItem = EntityCommon.getItemAtLocation(itemEnchant.getLocation());
            assertNull(enchantedItem, "item found at spell location when caster did not have item");

            // test player holding valid item
            caster.getInventory().setItemInOffHand(createValidItem(itemEnchant, 1));
            itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);
            mockServer.getScheduler().performTicks(2);
            assertTrue(itemEnchant.isKilled());
            enchantedItem = EntityCommon.getItemAtLocation(itemEnchant.getLocation());
            assertNotNull(enchantedItem, "Enchanted item not dropped");
            assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(enchantedItem), "valid item type was not enchanted");
            assertEquals(itemEnchant.getEnchantmentType(), Ollivanders2API.getItems().enchantedItems.getEnchantmentType(enchantedItem.getItemStack()), "enchantment type not expected");
        }
    }

    /**
     * Verify enchanting a stack of two leaves one enchanted item and a remainder stack of one.
     */
    @Test
    void enchantStackWithMultipleItemsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        ItemStack itemStack = createValidItem(itemEnchant, 2);
        if (itemEnchant.isNoProjectile()) {
            caster.getInventory().setItemInOffHand(itemStack);
        }
        else {
            testWorld.dropItem(targetLocation, itemStack);
        }

        mockServer.getScheduler().performTicks(20);
        assertTrue(itemEnchant.isKilled());

        ItemStack orig = null;
        ItemStack enchanted = null;

        for (Item item : itemEnchant.getNearbyItems(O2Spell.defaultRadius)) {
            if (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)) {
                enchanted = item.getItemStack();
            }
            else
                orig = item.getItemStack();
        }
        assertNotNull(enchanted, "Enchanted item not dropped");
        assertNotNull(orig, "Remainder of original not dropped");
        assertEquals(1, orig.getAmount(), "Unexpected remainder amount");
    }

    /**
     * Verify the enchantment magnitude follows {@code (usesModifier / 10) * strength} and stays within its min/max
     * bounds across low, medium, and high skill.
     */
    @Test
    void magnitudeTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        double usesModifier = 4;
        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, usesModifier);
        assertTrue(itemEnchant.getMagnitude() >= itemEnchant.getMinMagnitude(), "magnitude < minMagnitude");
        assertTrue(itemEnchant.getMagnitude() <= itemEnchant.getMaxMagnitude(), "magnitude > maxMagnitude");

        usesModifier = 80;
        itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, usesModifier);
        assertEquals((int) ((usesModifier / 10) * itemEnchant.getStrength()), itemEnchant.getMagnitude(), "unexpected magnitude");

        usesModifier = 200;
        itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, usesModifier);
        assertTrue(itemEnchant.getMagnitude() >= itemEnchant.getMinMagnitude(), "magnitude < minMagnitude");
        assertTrue(itemEnchant.getMagnitude() <= itemEnchant.getMaxMagnitude(), "magnitude > maxMagnitude");
    }

    /**
     * Verify canBeEnchanted accepts a valid item but rejects wands and invalid item types.
     */
    @Test
    void canBeEnchantedTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        ItemStack itemStack = createValidItem(itemEnchant, 1);
        assertTrue(itemEnchant.canBeEnchanted(itemStack), "valid item stack cannot be enchanted");

        itemStack = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.getRandomWood(), O2WandCoreType.getRandomCore(), 1);
        assertNotNull(itemStack);
        assertFalse(itemEnchant.canBeEnchanted(itemStack), "wand can be enchanted when it shouldn't be");

        itemStack = createInvalidItem();
        assertFalse(itemEnchant.canBeEnchanted(itemStack), "invalid item stack can be enchanted");
    }

    /**
     * Subclasses verify the spell-specific enchantment arguments stored with the item (e.g. book content for CELATUM,
     * destination coordinates for PORTUS).
     */
    @Test
    abstract void createEnchantmentArgsTest();

    /**
     * Subclasses verify any spell-specific alteration of the enchanted item (e.g. clearing pages for CELATUM); most
     * spells alter nothing and can leave this empty.
     */
    @Test
    abstract void alterItemTest();

    /**
     * No-op: item enchantments have no revert action.
     */
    @Override
    @Test
    void revertTest() {
        // item enchantments don't have revert actions
    }
}
