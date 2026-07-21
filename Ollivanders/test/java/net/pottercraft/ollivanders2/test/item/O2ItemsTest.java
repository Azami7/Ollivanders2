package net.pottercraft.ollivanders2.test.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.item.enchantment.EnchantedItems;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link O2Items}.
 *
 * @see O2Items
 * @see O2ItemType
 */
public class O2ItemsTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Assert the item stack carries the O2Items type NBT tag.
     *
     * @param itemStack the ItemStack to validate
     */
    private void checkNBTItemTag(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "item meta is null");
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        assertTrue(container.has(O2Items.o2ItemTypeKey), "Item does not have O2Items.o2ItemTypeKey set in NBT tags");
    }

    /**
     * Assert the item stack's display name matches the expected name.
     *
     * @param expectedName the expected display name
     * @param itemStack    the ItemStack to validate
     */
    private void checkItemName(@NotNull String expectedName, @NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "item meta is null");
        assertEquals(expectedName, itemMeta.getDisplayName(), "item stack display name is incorrect");
    }

    /**
     * Assert the item stack contains the expected amount.
     *
     * @param amount    the expected amount
     * @param itemStack the ItemStack to validate
     */
    private void checkAmount(int amount, @NotNull ItemStack itemStack) {
        assertEquals(amount, itemStack.getAmount(), "item stack has the wrong amount");
    }

    /**
     * Assert the item stack's material matches the expected material.
     *
     * @param expectedMaterial the expected material
     * @param itemStack        the ItemStack to validate
     */
    private void checkMaterial(@NotNull Material expectedMaterial, @NotNull ItemStack itemStack) {
        assertEquals(expectedMaterial, itemStack.getType(), "item stack is the wrong material");
    }

    /**
     * Assert the first line of the item stack's lore matches the expected text.
     *
     * @param expectedLore the expected first lore line
     * @param itemStack    the ItemStack to validate
     */
    private void checkLore(@NotNull String expectedLore, ItemStack itemStack) {
        assertNotNull(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "item meta is null");

        List<String> lore = itemMeta.getLore();
        assertNotNull(lore, "item lore not set");
        assertEquals(expectedLore, lore.getFirst(), "lore did not match expected");
    }

    /**
     * Assert the item stack matches the expected type's material, name, and NBT tag at the given amount.
     *
     * @param itemType  the expected O2ItemType
     * @param amount    the expected stack amount
     * @param itemStack the ItemStack to validate
     */
    private void checkItemStackByItemType(@NotNull O2ItemType itemType, int amount, ItemStack itemStack) {
        assertNotNull(itemStack);
        checkMaterial(itemType.getMaterial(), itemStack);
        checkAmount(amount, itemStack);
        checkItemName(itemType.getName(),itemStack);
        checkNBTItemTag(itemStack);
    }

    /**
     * Assert the item stack carries all four enchantment NBT tags (type, ID, magnitude, args).
     *
     * @param itemStack the ItemStack to validate
     */
    private void checkEnchantmentNBT(ItemStack itemStack) {
        assertNotNull(itemStack, "Ollivanders2API.getItems().getItemByType(itemType, amount) returned null");
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "item meta is null");

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        assertTrue(container.has(EnchantedItems.enchantmentType), "Item does not have EnchantedItems.enchantmentType set in NBT tags");
        assertTrue(container.has(EnchantedItems.enchantmentID), "Item does not have EnchantedItems.enchantmentID set in NBT tags");
        assertTrue(container.has(EnchantedItems.enchantmentMagnitude), "Item does not have EnchantedItems.enchantmentMagnitude set in NBT tags");
        assertTrue(container.has(EnchantedItems.enchantmentArgs), "Item does not have EnchantedItems.enchantmentArgs set in NBT tags");
    }

    /**
     * getItemNameByType() returns the item's name for both basic and enchanted types.
     */
    @Test
    void getItemNameByTypeTest() {
        // test 1: basic item
        O2ItemType expected = O2ItemType.GINGER_ROOT;
        String itemName = Ollivanders2API.getItems().getItemNameByType(expected);
        assertNotNull(itemName, "Ollivanders2API.getItems().getItemNameByType(expected) returned null");
        assertEquals(expected.getName(), itemName, "Ollivanders2API.getItems().getItemNameByType(expected) did not return expected type");

        // test 2: enchanted item
        expected = O2ItemType.BROOMSTICK;
        itemName = Ollivanders2API.getItems().getItemNameByType(expected);
        assertNotNull(itemName, "Ollivanders2API.getItems().getItemNameByType(expected) returned null");
        assertEquals(expected.getName(), itemName, "Ollivanders2API.getItems().getItemNameByType(expected) did not return expected type");
    }

    /**
     * getItemTypeByName() resolves a type by name case-insensitively, and returns null for an unknown name.
     */
    @Test
    void getItemTypeByNameTest() {
        // test 1: basic item
        O2ItemType expected = O2ItemType.GINGER_ROOT;
        O2ItemType itemType = Ollivanders2API.getItems().getItemTypeByName(expected.getName());
        assertNotNull(itemType, "Ollivanders2API.getItems().getItemTypeByName(expected.getName()) returned null");
        assertEquals(expected, itemType, "Ollivanders2API.getItems().getItemTypeByName(expected.getName()) did not return expected type");

        // test 2: enchanted item
        expected = O2ItemType.BROOMSTICK;
        itemType = Ollivanders2API.getItems().getItemTypeByName(expected.getName());
        assertNotNull(itemType, "Ollivanders2API.getItems().getItemTypeByName(expected.getName()) returned null");
        assertEquals(expected, itemType, "Ollivanders2API.getItems().getItemTypeByName(expected.getName()) did not return expected type");

        // test 3: case-insensitive
        itemType = Ollivanders2API.getItems().getItemTypeByName("broomstick");
        assertNotNull(itemType, "Ollivanders2API.getItems().getItemTypeByName(\"broomstick\") returned null");
        assertEquals(O2ItemType.BROOMSTICK, itemType, "getItemTypeByName(\"broomstick\") did not return BROOMSTICK");

        // test 4: invalid name
        itemType = Ollivanders2API.getItems().getItemTypeByName("Invalid Name");
        assertNull(itemType, "Ollivanders2API.getItems().getItemTypeByName(\"Invalid Name\") did not return null");
    }

    /**
     * getItemTypeByNameStartsWith() resolves a type by a case-insensitive name prefix, and returns null for no match.
     */
    @Test
    void getItemTypeByNameStartsWithTest() {
        // test 1: basic item with unique prefix
        String nameStartsWith = "Chiz";
        O2ItemType itemType = Ollivanders2API.getItems().getItemTypeByNameStartsWith(nameStartsWith);
        assertNotNull(itemType, "Ollivanders2API.getItems().getItemTypeByNameStartsWith(\"Chiz\") returned null");
        assertEquals(O2ItemType.CHIZPURFLE_FANGS, itemType, "getItemTypeByNameStartsWith(\"Chiz\") did not return CHIZPURFLE_FANGS");

        // test 2: ambiguous name - should return one of the matching items (order not guaranteed due to HashMap)
        nameStartsWith = "Crushed";
        itemType = Ollivanders2API.getItems().getItemTypeByNameStartsWith(nameStartsWith);
        assertNotNull(itemType, "Ollivanders2API.getItems().getItemTypeByNameStartsWith(\"Crushed\") returned null");
        assertTrue(itemType.getName().startsWith("Crushed"), "getItemTypeByNameStartsWith(\"Crushed\") did not return an item starting with \"Crushed\"");

        // test 3: case-insensitive
        nameStartsWith = "DOXY";
        itemType = Ollivanders2API.getItems().getItemTypeByNameStartsWith(nameStartsWith);
        assertNotNull(itemType, "Ollivanders2API.getItems().getItemTypeByNameStartsWith(\"DOXY\") returned null");
        assertEquals(O2ItemType.DOXY_VENOM, itemType, "getItemTypeByNameStartsWith(\"DOXY\") did not return DOXY_VENOM");

        // test 4: invalid name
        itemType = Ollivanders2API.getItems().getItemTypeByNameStartsWith("Invalid Item");
        assertNull(itemType, "Ollivanders2API.getItems().getItemTypeByNameStartsWith(\"Invalid Item\") did not return null");
    }

    /**
     * getItemMaterialByType() returns the backing material for basic, enchanted, and custom-appearance items.
     */
    @Test
    void getItemMaterialByTypeTest() {
        // test 1: basic item
        O2ItemType itemType = O2ItemType.DEW_DROP;
        Material material = Ollivanders2API.getItems().getItemMaterialByType(itemType);
        assertNotNull(material, "Ollivanders2API.getItems().getItemMaterialByType(DEW_DROP) returned null");
        assertEquals(itemType.getMaterial(), material, "getItemMaterialByType(DEW_DROP) returned wrong material");

        // test 2: enchanted item
        itemType = O2ItemType.BROOMSTICK;
        material = Ollivanders2API.getItems().getItemMaterialByType(itemType);
        assertNotNull(material, "Ollivanders2API.getItems().getItemMaterialByType(BROOMSTICK) returned null");
        assertEquals(itemType.getMaterial(), material, "getItemMaterialByType(BROOMSTICK) returned wrong material");

        // test 3: item with custom lore
        itemType = O2ItemType.INVISIBILITY_CLOAK;
        material = Ollivanders2API.getItems().getItemMaterialByType(itemType);
        assertNotNull(material, "Ollivanders2API.getItems().getItemMaterialByType(INVISIBILITY_CLOAK) returned null");
        assertEquals(Material.CHAINMAIL_CHESTPLATE, material, "getItemMaterialByType(INVISIBILITY_CLOAK) returned wrong material");
    }

    /**
     * getO2ItemNameFromItemStack() returns the tagged name of an O2Item, and null for vanilla items even when renamed.
     */
    @Test
    void getO2ItemNameFromItemStackTest() {
        // test 1: basic O2Item should return its name
        O2ItemType itemType = O2ItemType.GINGER_ROOT;
        ItemStack itemStack = Ollivanders2API.getItems().getItemByType(itemType, 1);
        assertNotNull(itemStack, "Ollivanders2API.getItems().getItemByType() returned null");
        String itemName = Ollivanders2API.getItems().getO2ItemNameFromItemStack(itemStack);
        assertNotNull(itemName, "getO2ItemNameFromItem() returned null for O2Item");
        assertEquals(itemType.getName(), itemName, "getO2ItemNameFromItem() did not return expected name");

        // test 2: vanilla ItemStack should return null (no NBT tag)
        ItemStack vanillaItem = new ItemStack(Material.DIAMOND, 1);
        itemName = Ollivanders2API.getItems().getO2ItemNameFromItemStack(vanillaItem);
        assertNull(itemName, "getO2ItemNameFromItem() did not return null for vanilla item");

        // test 3: enchanted O2Item should return its name
        itemType = O2ItemType.BROOMSTICK;
        itemStack = Ollivanders2API.getItems().getItemByType(itemType, 1);
        assertNotNull(itemStack, "Ollivanders2API.getItems().getItemByType() returned null for enchanted item");
        itemName = Ollivanders2API.getItems().getO2ItemNameFromItemStack(itemStack);
        assertNotNull(itemName, "getO2ItemNameFromItem() returned null for enchanted O2Item");
        assertEquals(itemType.getName(), itemName, "getO2ItemNameFromItem() did not return expected name for enchanted item");

        // test 4: renamed vanilla item should still return null (no NBT tag)
        ItemStack renamedItem = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta meta = renamedItem.getItemMeta();
        assertNotNull(meta);
        meta.setDisplayName("Galleon");
        renamedItem.setItemMeta(meta);
        itemName = Ollivanders2API.getItems().getO2ItemNameFromItemStack(renamedItem);
        assertNull(itemName, "getO2ItemNameFromItem() did not return null for renamed vanilla item");
    }

    /**
     * getItemByType() builds a fully configured stack (incl. lore and enchantment NBT), and returns null for a negative
     * amount.
     */
    @Test
    void getItemByTypeTest() {
        // test 1: basic item
        O2ItemType itemType = O2ItemType.ACONITE;
        int amount = 1;
        ItemStack itemStack = Ollivanders2API.getItems().getItemByType(itemType, amount);
        checkItemStackByItemType(itemType, amount, itemStack);

        // test 2: item with lore
        amount = 4;
        itemType = O2ItemType.GALLEON;
        itemStack = Ollivanders2API.getItems().getItemByType(itemType, amount);
        checkItemStackByItemType(itemType, amount, itemStack);
        checkLore(O2ItemType.GALLEON.getLore(), itemStack);

        // test 3: enchanted item
        itemType = O2ItemType.BROOMSTICK;
        itemStack = Ollivanders2API.getItems().getItemByType(itemType, 1);
        checkItemStackByItemType(itemType, 1, itemStack);
        checkEnchantmentNBT(itemStack);

        // test 4: amount < 0
        itemStack = Ollivanders2API.getItems().getItemByType(itemType, -1);
        assertNull(itemStack, "item stack was not null when amount < 0");
    }

    /**
     * getItemByName() builds a stack by name case-insensitively, and returns null for an unknown name.
     */
    @Test
    void getItemByNameTest() {
        // test 1: basic item
        O2ItemType itemType = O2ItemType.BEZOAR;
        int amount = 1;
        ItemStack itemStack = Ollivanders2API.getItems().getItemByName(itemType.getName(), amount);
        checkItemStackByItemType(itemType, amount, itemStack);

        // test 2: enchanted item
        itemType = O2ItemType.BROOMSTICK;
        amount = 4;
        itemStack = Ollivanders2API.getItems().getItemByName(itemType.getName(), amount);
        checkItemStackByItemType(itemType, amount, itemStack);

        // test 3: case-insensitive
        itemStack = Ollivanders2API.getItems().getItemByName(itemType.getName().toLowerCase(), amount);
        checkItemStackByItemType(itemType, amount, itemStack);

        // test 4: invalid name
        itemStack = Ollivanders2API.getItems().getItemByName("Invalid Item", 1);
        assertNull(itemStack, "Ollivanders2API.getItems().getItemByName(\"Invalid Item\", 1) did not return null");
    }

    /**
     * getItemByNameStartsWith() builds a stack by a case-insensitive name prefix, and returns null for no match.
     */
    @Test
    void getItemByNameStartsWithTest() {
        // test 1: basic item
        String nameStartsWith = "Chiz";
        ItemStack itemStack = Ollivanders2API.getItems().getItemByNameStartsWith(nameStartsWith, 1);
        checkItemStackByItemType(O2ItemType.CHIZPURFLE_FANGS, 1, itemStack);

        // test 2: ambiguous name - should return one of the matching items (order not guaranteed due to HashMap)
        nameStartsWith = "Crushed";
        itemStack = Ollivanders2API.getItems().getItemByNameStartsWith(nameStartsWith, 1);
        assertNotNull(itemStack, "Ollivanders2API.getItems().getItemByNameStartsWith(\"Crushed\", 1) returned null");
        checkAmount(1, itemStack);
        checkNBTItemTag(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.getDisplayName().startsWith("Crushed"), "Item name did not start with \"Crushed\"");

        // test 3: case-insensitive
        nameStartsWith = "DOXY";
        itemStack = Ollivanders2API.getItems().getItemByNameStartsWith(nameStartsWith, 1);
        checkItemStackByItemType(O2ItemType.DOXY_VENOM, 1, itemStack);

        // test 4: invalid name
        itemStack = Ollivanders2API.getItems().getItemByNameStartsWith("Invalid Item", 1);
        assertNull(itemStack, "Ollivanders2API.getItems().getItemByNameStartsWith(\"Invalid Item\", 1) did not return null");
    }

    /**
     * getAllItemNames() returns one name per item type, sorted alphabetically.
     */
    @Test
    void getAllItemNamesTest() {
        ArrayList<String> itemNames = Ollivanders2API.getItems().getAllItemNames();
        assertFalse(itemNames.isEmpty(), "Ollivanders2API.getItems().getAllItemNames() returned empty list");
        assertEquals(O2ItemType.values().length, itemNames.size(), "Ollivanders2API.getItems().getAllItemNames() returned list did not have expected number of items");

        // verify all item names are present
        for (O2ItemType itemType : O2ItemType.values()) {
            assertTrue(itemNames.contains(itemType.getName()));
        }

        // verify list is sorted alphabetically
        for (int i = 0; i < itemNames.size() - 1; i++) {
            assertTrue(itemNames.get(i).compareTo(itemNames.get(i + 1)) <= 0,
                    "List is not sorted: \"" + itemNames.get(i) + "\" should come before \"" + itemNames.get(i + 1) + "\"");
        }
    }

    /**
     * getItemTypeByItem() is a pass-through to {@link #getItemTypeByItemStackTest()}, so it needs no separate assertions.
     */
    @Test
    void getItemTypeByItemTest() {
        // this is just a pass-through to getItemTypeByItemStack so we do not need a separate test
    }

    /**
     * getItemTypeByItemStack() returns the type of a tagged O2Item, and null for vanilla items even when renamed.
     */
    @Test
    void getItemTypeByItemStackTest() {
        // test 1: valid O2Item should return its O2ItemType
        O2ItemType expectedType = O2ItemType.GINGER_ROOT;
        ItemStack itemStack = Ollivanders2API.getItems().getItemByType(expectedType, 1);
        assertNotNull(itemStack, "Ollivanders2API.getItems().getItemByType() returned null");
        O2ItemType itemType = Ollivanders2API.getItems().getItemTypeByItemStack(itemStack);
        assertNotNull(itemType, "getItemTypeByItemStack() returned null for O2Item");
        assertEquals(expectedType, itemType, "getItemTypeByItemStack() did not return expected type");

        // test 2: vanilla ItemStack should return null (no NBT tag)
        ItemStack vanillaItem = new ItemStack(Material.DIAMOND, 1);
        itemType = Ollivanders2API.getItems().getItemTypeByItemStack(vanillaItem);
        assertNull(itemType, "getItemTypeByItemStack() did not return null for vanilla item");

        // test 3: enchanted O2Item should return its O2ItemType
        expectedType = O2ItemType.BROOMSTICK;
        itemStack = Ollivanders2API.getItems().getItemByType(expectedType, 1);
        assertNotNull(itemStack, "Ollivanders2API.getItems().getItemByType() returned null for enchanted item");
        itemType = Ollivanders2API.getItems().getItemTypeByItemStack(itemStack);
        assertNotNull(itemType, "getItemTypeByItemStack() returned null for enchanted O2Item");
        assertEquals(expectedType, itemType, "getItemTypeByItemStack() did not return expected type for enchanted item");

        // test 4: renamed vanilla item should return null (no NBT tag)
        ItemStack renamedItem = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta meta = renamedItem.getItemMeta();
        assertNotNull(meta);
        meta.setDisplayName("Galleon");
        renamedItem.setItemMeta(meta);
        itemType = Ollivanders2API.getItems().getItemTypeByItemStack(renamedItem);
        assertNull(itemType, "getItemTypeByItemStack() did not return null for renamed vanilla item");
    }

    /**
     * Test that custom recipes were loaded
     */
    @Test
    void shapedRecipeTest() {
        HashMap<O2ItemType, NamespacedKey> recipeKeys = Ollivanders2API.getItems().getRecipeKeys();
        assertFalse(recipeKeys.isEmpty(), "no recipe keys");

        for (O2ItemType itemType : recipeKeys.keySet()) {
            Recipe recipe = mockServer.getRecipe(recipeKeys.get(itemType));
            assertNotNull(recipe, "did not find recipe for " + itemType.getName());

            ItemStack itemStack = itemType.getItem(1);
            assertNotNull(itemStack);
            ItemStack recipeResult = recipe.getResult();

            assertEquals(itemStack.getType(), recipeResult.getType(), "unexpected item type for recipe");
            assertEquals(itemStack.getItemMeta().displayName(), recipeResult.getItemMeta().displayName(), "unexpected item type for recipe");
        }
    }

    /**
     * getWands() just returns the wand manager, which is covered by its own test class.
     */
    @Test
    void getWandsTest() {
        // this just returns the wand manager class, wand functions tested in its own test class
    }

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
