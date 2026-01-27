package net.pottercraft.ollivanders2.test.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.item.enchantment.EnchantedItems;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the O2Items item management system.
 * <p>
 * This test class verifies the functionality of the {@link O2Items} class, which provides methods for
 * creating, retrieving, and identifying Ollivanders2 custom items. The tests cover the complete lifecycle
 * of item management including name-based lookups, type-based retrieval, item creation with proper NBT tags,
 * and support for both basic items and enchanted items.
 * </p>
 * <p>
 * The test suite uses MockBukkit to simulate a Bukkit server environment and tests the following capabilities:
 * <ul>
 * <li>Item name resolution and case-insensitive lookups</li>
 * <li>Item type resolution by exact name and name prefix matching</li>
 * <li>Material identification for different item types</li>
 * <li>ItemStack creation with proper display names, amounts, and NBT tags</li>
 * <li>Support for items with custom lore and enchanted items</li>
 * <li>Reverse lookups to identify item types from ItemStack instances</li>
 * <li>Comprehensive item listing and sorting</li>
 * </ul>
 * </p>
 *
 * @author Test Suite
 * @see O2Items
 * @see O2ItemType
 * @see Ollivanders2API
 */
public class O2ItemsTest {
    /**
     * Mock Bukkit server instance used for all tests.
     * <p>
     * Set up during {@link #globalSetUp()} and torn down during {@link #globalTearDown()}.
     * This allows tests to interact with Minecraft server objects without requiring an actual server.
     * </p>
     */
    static ServerMock mockServer;

    /**
     * The Ollivanders2 plugin instance loaded with default test configuration.
     * <p>
     * Initialized during {@link #globalSetUp()} to provide access to plugin functionality and
     * allow proper initialization of all O2Items through the plugin's startup sequence.
     * </p>
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
     * Verify that an ItemStack has the required O2Items NBT tag.
     * <p>
     * Checks that the item's persistent data container contains the O2Items NBT key, which is required for
     * proper item identification and type resolution. This assertion ensures that items created through the
     * O2Items system are properly tagged.
     * </p>
     *
     * @param itemStack the ItemStack to validate for O2Items NBT tags
     */
    private void checkNBTItemTag(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "item meta is null");
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        assertTrue(container.has(O2Items.o2ItemTypeKey), "Item does not have O2Items.o2ItemTypeKey set in NBT tags");
    }

    /**
     * Verify that an ItemStack has the expected display name.
     * <p>
     * Asserts that the item's display name matches the provided expected name. This is used to verify
     * that items are properly named when created through the O2Items system.
     * </p>
     *
     * @param expectedName the expected display name of the item
     * @param itemStack the ItemStack to validate
     */
    private void checkItemName(@NotNull String expectedName, @NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "item meta is null");
        assertEquals(expectedName, itemMeta.getDisplayName(), "item stack display name is incorrect");
    }

    /**
     * Verify that an ItemStack has the expected stack amount.
     * <p>
     * Asserts that the number of items in the stack matches the provided expected amount.
     * </p>
     *
     * @param amount the expected amount in the ItemStack
     * @param itemStack the ItemStack to validate
     */
    private void checkAmount(int amount, @NotNull ItemStack itemStack) {
        assertEquals(amount, itemStack.getAmount(), "item stack has the wrong amount");
    }

    /**
     * Verify that an ItemStack is constructed from the expected Minecraft material.
     * <p>
     * Asserts that the item's type matches the provided expected material. This ensures items are
     * created with the correct base Minecraft block or item material.
     * </p>
     *
     * @param expectedMaterial the expected Minecraft material type
     * @param itemStack the ItemStack to validate
     */
    private void checkMaterial(@NotNull Material expectedMaterial, @NotNull ItemStack itemStack) {
        assertEquals(expectedMaterial, itemStack.getType(), "item stack is the wrong material");
    }

    /**
     * Verify that an ItemStack has the expected lore text.
     * <p>
     * Asserts that the first line of the item's lore matches the provided expected lore string.
     * Lore is displayed below the item name when hovering over it in the inventory.
     * </p>
     *
     * @param expectedLore the expected first line of lore text
     * @param itemStack the ItemStack to validate (may be null)
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
     * Verify that an ItemStack matches all expected properties of an O2ItemType.
     * <p>
     * Performs comprehensive validation of an ItemStack including material, amount, display name,
     * and NBT tags. This is a convenience method that combines multiple checks to verify an entire
     * ItemStack creation.
     * </p>
     *
     * @param itemType the expected O2ItemType
     * @param amount the expected stack amount
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
     * Verify that an ItemStack has all required enchantment NBT tags.
     * <p>
     * Checks that the item's persistent data container contains all four required enchantment tags:
     * enchantmentType, enchantmentID, enchantmentMagnitude, and enchantmentArgs. This ensures that
     * enchanted items are properly configured with all necessary enchantment metadata.
     * </p>
     *
     * @param itemStack the ItemStack to validate for enchantment tags
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
     * Test that getItemNameByType returns the correct item name for a given O2ItemType.
     * <p>
     * Verifies the ability to retrieve a human-readable name from an O2ItemType enum constant.
     * This method acts as a simple accessor to the underlying name data stored in the enum.
     * </p>
     * <ul>
     * <li>Basic item name retrieval for simple items</li>
     * <li>Enchanted item name retrieval for complex enchanted items</li>
     * </ul>
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
     * Test that getItemTypeByName resolves item types from names with case-insensitivity and error handling.
     * <p>
     * Verifies the ability to look up an O2ItemType by its human-readable name. This is a critical
     * function for user-facing commands and UI elements where users provide item names as input.
     * </p>
     * <ul>
     * <li>Basic item type resolution by exact name</li>
     * <li>Enchanted item type resolution</li>
     * <li>Case-insensitive name matching</li>
     * <li>Handling of invalid names returning null</li>
     * </ul>
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
     * Test that getItemTypeByNameStartsWith finds items by partial name matching.
     * <p>
     * Verifies prefix-based item lookup functionality for cases where users provide incomplete item names.
     * This method is useful for autocomplete features and flexible item lookups.
     * </p>
     * <ul>
     * <li>Unique prefix matching returning a specific item</li>
     * <li>Ambiguous prefix matching returning one of the matching items</li>
     * <li>Case-insensitive prefix matching</li>
     * <li>Handling of invalid prefixes returning null</li>
     * </ul>
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
     * Test that getItemMaterialByType correctly identifies the Minecraft material for each item type.
     * <p>
     * Verifies that items are mapped to their appropriate Minecraft materials, including support for
     * basic items, enchanted items, and items with custom appearances like the invisibility cloak.
     * </p>
     * <ul>
     * <li>Basic item material resolution</li>
     * <li>Enchanted item material resolution</li>
     * <li>Custom material mapping for special items</li>
     * </ul>
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
     * Test that getO2ItemNameFromItemStack extracts O2Items names from ItemStack instances.
     * <p>
     * Verifies that the system can identify O2Items by checking for the required NBT tags and extract
     * their names, while correctly returning null for vanilla items (even if renamed). This is essential
     * for distinguishing custom items from standard Minecraft items.
     * </p>
     * <ul>
     * <li>Name extraction from basic O2Items</li>
     * <li>Null return for vanilla items without O2Items tags</li>
     * <li>Name extraction from enchanted O2Items</li>
     * <li>Null return for renamed vanilla items</li>
     * </ul>
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
     * Test that getItemByType creates fully-configured ItemStack instances from O2ItemType.
     * <p>
     * Verifies complete ItemStack creation including material, amount, display name, lore, NBT tags,
     * and enchantment metadata. This is the primary method for creating items to give to players.
     * Also validates amount validation (rejecting negative amounts).
     * </p>
     * <ul>
     * <li>Basic item creation with correct properties</li>
     * <li>Item creation with custom lore</li>
     * <li>Enchanted item creation with enchantment NBT tags</li>
     * <li>Amount validation (negative amounts return null)</li>
     * </ul>
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
     * Test that getItemByName creates ItemStacks from human-readable item names.
     * <p>
     * Verifies that items can be created by their display names, including case-insensitive matching
     * and proper error handling for invalid names. This allows players and commands to specify items
     * using readable names instead of enum constants.
     * </p>
     * <ul>
     * <li>Basic item creation from exact name</li>
     * <li>Enchanted item creation from name</li>
     * <li>Case-insensitive name matching</li>
     * <li>Null return for invalid names</li>
     * </ul>
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
     * Test that getItemByNameStartsWith creates ItemStacks using partial item name matching.
     * <p>
     * Verifies that ItemStacks can be created using prefix matching on item names, including
     * case-insensitive matching and proper validation. This enables flexible item creation where
     * users only need to provide the beginning of an item name.
     * </p>
     * <ul>
     * <li>ItemStack creation from unique prefix</li>
     * <li>ItemStack creation from ambiguous prefix (returns one match)</li>
     * <li>Case-insensitive prefix matching</li>
     * <li>Null return for invalid prefixes</li>
     * </ul>
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
     * Test that getAllItemNames returns a complete alphabetically-sorted list of all item names.
     * <p>
     * Verifies that the complete list of all O2ItemType names is available and properly formatted.
     * This is useful for help menus, autocomplete features, and displaying available items to players.
     * </p>
     * <ul>
     * <li>Non-empty list is returned</li>
     * <li>List contains exactly one entry per O2ItemType</li>
     * <li>All O2ItemType names are present in the list</li>
     * <li>List is sorted alphabetically in ascending order</li>
     * </ul>
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
     * Test placeholder for getItemTypeByItem method.
     * <p>
     * This method is a pass-through to {@link O2Items#getItemTypeByItemStack(ItemStack)} and does not
     * require separate testing. The underlying functionality is verified in {@link #getItemTypeByItemStackTest()}.
     * </p>
     */
    @Test
    void getItemTypeByItemTest() {
        // this is just a pass-through to getItemTypeByItemStack so we do not need a separate test
    }

    /**
     * Test that getItemTypeByItemStack correctly identifies O2ItemType from ItemStack instances.
     * <p>
     * Verifies the reverse lookup capability that determines whether an ItemStack is an O2Item and
     * retrieves its type. This is essential for item identification in game mechanics like spell
     * ingredient checking and inventory management.
     * </p>
     * <ul>
     * <li>Valid O2Item returns the correct O2ItemType</li>
     * <li>Vanilla ItemStack returns null (no NBT tag)</li>
     * <li>Enchanted O2Item returns the correct O2ItemType</li>
     * <li>Renamed vanilla item returns null (display name alone is not sufficient)</li>
     * </ul>
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
     * Test placeholder for getWands method.
     * <p>
     * This method simply returns the {@link net.pottercraft.ollivanders2.item.wand.O2Wands} manager instance.
     * Wand functionality is tested in its own dedicated test class.
     * </p>
     */
    @Test
    void getWandsTest() {
        // this just returns the wand manager class, wand functions tested in its own test class
    }

    /**
     * Reset test state after each test method.
     * <p>
     * Ensures the debug flag is disabled after each test to prevent debug output from affecting
     * subsequent tests or polluting test logs.
     * </p>
     */
    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    /**
     * Clean up MockBukkit server after all tests complete.
     * <p>
     * Releases MockBukkit resources and unloads the mock server. This must be called after all
     * tests in the class have finished to properly clean up the test environment and allow
     * other test classes to create fresh mock servers.
     * </p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
