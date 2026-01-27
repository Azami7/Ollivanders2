package net.pottercraft.ollivanders2.test.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.O2Item;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the O2ItemType enum.
 *
 * <p>Tests the custom item type system that defines all special items in Ollivanders2, including
 * potion ingredients, wand cores, wizard money, enchanted items, and divination objects. Verifies
 * that item properties (name, lore, material, enchantment) are correctly configured and that
 * item lookup and creation methods work as expected.</p>
 *
 * <p>Test Categories:</p>
 * <ul>
 * <li><strong>Property Accessors:</strong> getName(), getLore(), getMaterial(), getItemEnchantment()</li>
 * <li><strong>Property Mutators:</strong> setMaterial() for configurable materials</li>
 * <li><strong>Lookup Methods:</strong> getTypeByName() for finding items by display name</li>
 * <li><strong>Type Checking:</strong> isItemThisType() for verifying ItemStack types via NBT</li>
 * <li><strong>Item Creation:</strong> getItem() for creating ItemStacks with proper metadata</li>
 * <li><strong>Data Integrity:</strong> Verifying all items have unique names</li>
 * </ul>
 */
public class O2ItemTypeTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test that getName() returns the correct display name for items.
     *
     * <p>Verifies item names for:</p>
     * <ul>
     * <li>Items with custom names different from Minecraft material (DEW_DROP)</li>
     * <li>Items with names matching their Minecraft material (BONE)</li>
     * <li>Enchanted items (BROOMSTICK)</li>
     * </ul>
     */
    @Test
    void getNameTest() {
        O2ItemType itemType = O2ItemType.DEW_DROP;
        assertEquals("Dew Drop", itemType.getName(), "Item name for a renamed item incorrect");

        itemType = O2ItemType.BONE;
        assertEquals("Bone", itemType.getName(), "Item name for same-named item incorrect");

        itemType= O2ItemType.BROOMSTICK;
        assertEquals("Broomstick", itemType.getName(), "Item name for enchanted item incorrect");
    }

    /**
     * Test that getLore() returns the correct lore string for items.
     *
     * <p>Verifies:</p>
     * <ul>
     * <li>Items without lore return their name as the lore (DEW_DROP)</li>
     * <li>Items with custom lore return that lore string (INVISIBILITY_CLOAK)</li>
     * </ul>
     */
    @Test
    void getLoreTest() {
        // test 1: null lore
        O2ItemType itemType = O2ItemType.DEW_DROP;
        assertEquals(itemType.getName(), itemType.getLore(), "Lore for item with null lore was not item name");

        // test 2: lore set
        itemType = O2ItemType.INVISIBILITY_CLOAK;
        assertEquals("Silvery Transparent Cloak", itemType.getLore(), "getLore() did not return expected string for O2ItemType.INVISIBILITY_CLOAK");
    }

    /**
     * Test that getMaterial() returns the correct Bukkit Material for items.
     *
     * <p>Verifies material types for regular items and enchanted items.</p>
     */
    @Test
    void getMaterialTypeTest() {
        // test 1: not enchanted item
        O2ItemType itemType = O2ItemType.DEW_DROP;
        assertEquals(Material.GHAST_TEAR, itemType.getMaterial(), "Material type for O2ItemType.DEW_DROP not correct.");

        // test 2: enchanted item
        itemType = O2ItemType.INVISIBILITY_CLOAK;
        assertEquals(Material.CHAINMAIL_CHESTPLATE, itemType.getMaterial(), "Material type for O2ItemType.INVISIBILITY_CLOAK not correct.");
    }

    /**
     * Test that getItemEnchantment() returns the correct enchantment for items.
     *
     * <p>Verifies:</p>
     * <ul>
     * <li>Non-enchanted items return null (DEW_DROP)</li>
     * <li>Enchanted items return the correct ItemEnchantmentType (BROOMSTICK has VOLATUS)</li>
     * </ul>
     */
    @Test
    void getItemEnchantmentTest() {
        // test 1: not enchanted item
        O2ItemType itemType = O2ItemType.DEW_DROP;
        assertNull(itemType.getItemEnchantment(), "getItemEnchantment() did not return null for O2ItemType.DEW_DROP");

        // test 2: enchanted item
        itemType = O2ItemType.BROOMSTICK;
        assertNotNull(itemType.getItemEnchantment(), "getItemEnchantment() returned null for O2ItemType.BROOMSTICK");
        assertEquals(ItemEnchantmentType.VOLATUS, itemType.getItemEnchantment(), "Wrong enchantment type for O2ItemType.BROOMSTICK, expected: " + ItemEnchantmentType.VOLATUS.getName() + ", actual: " + itemType.getItemEnchantment().getName());

        // test 3: a different enchanted item
        // to be added, right now only Broomstick has an enchantment
    }

    /**
     * Test that setMaterial() correctly changes the material for an item type.
     *
     * <p>Verifies that the material can be changed at runtime. This allows server admins to
     * customize which Minecraft material represents each O2ItemType. The test resets the
     * material after testing to avoid affecting other tests.</p>
     */
    @Test
    void setMaterialTest() {
        // test 1: test default value
        O2ItemType itemType = O2ItemType.FLOO_POWDER;
        Material originalMaterial = itemType.getMaterial();
        assertEquals(Material.REDSTONE, originalMaterial, "Default material type not correct for O2ItemType.FLOO_POWDER");

        // test 2: set to a new value
        itemType.setMaterial(Material.BLUE_CONCRETE_POWDER);
        assertEquals(Material.BLUE_CONCRETE_POWDER, itemType.getMaterial(), "Material type not changed when expected");

        // reset to original value to avoid affecting other tests
        itemType.setMaterial(originalMaterial);
    }

    /**
     * Test that getTypeByName() correctly finds item types by their display name.
     *
     * <p>Verifies:</p>
     * <ul>
     * <li>Items with names matching their Minecraft material (BONE)</li>
     * <li>Items with custom names (DEW_DROP)</li>
     * <li>Enchanted items (BROOMSTICK)</li>
     * <li>Invalid names return null</li>
     * <li>Case-insensitive matching works</li>
     * <li>Potion items can be found by name</li>
     * </ul>
     */
    @Test
    void getTypeByNameTest() {
        // test 1: item where name is the same as in MC
        O2ItemType expectedItemType = O2ItemType.BONE;
        O2ItemType itemType = O2ItemType.getTypeByName(expectedItemType.getName());
        assertEquals(expectedItemType, itemType, "O2ItemType.getTypeByName() did not return expected type for O2ItemType.BONE");

        // test 2: item where name is customized
        expectedItemType = O2ItemType.DEW_DROP;
        itemType = O2ItemType.getTypeByName(expectedItemType.getName());
        assertEquals(expectedItemType, itemType, "O2ItemType.getTypeByName() did not return expected type for O2ItemType.DEW_DROP");

        // test 3: enchanted item
        expectedItemType = O2ItemType.BROOMSTICK;
        itemType = O2ItemType.getTypeByName(expectedItemType.getName());
        assertEquals(expectedItemType, itemType, "O2ItemType.getTypeByName() did not return expected type for O2ItemType.BROOMSTICK");

        // test 4: invalid name
        itemType = O2ItemType.getTypeByName("Invalid Name");
        assertNull(itemType, "getTypeByName(\"Invalid Name\") did not return null");

        // test 5: case insensitive
        itemType = O2ItemType.getTypeByName("broomstick");
        assertNotNull(itemType, "O2ItemType.getTypeByName(\"broomstick\") returned null");
        assertEquals(O2ItemType.BROOMSTICK, itemType, "item type is not O2ItemType.BROOMSTICK");

        // test 6: potion item
        itemType = O2ItemType.getTypeByName("Sopophorus Bean Juice");
        assertNotNull(itemType, "O2ItemType.getTypeByName(\"Sopophorus Bean Juice\") returned null");
    }

    /**
     * Every item must have a unique name or things like getTypeByName() will have an ambiguous return value.
     *
     * <p>Groups all O2ItemType values by their name and checks for duplicates. If any name appears
     * more than once, the test fails and reports which names are duplicated.</p>
     */
    @Test
    void uniqueItemNamesTest() {
        Map<String, List<O2ItemType>> grouped = Arrays.stream(O2ItemType.values())
                .collect(Collectors.groupingBy(O2ItemType::getName));

        List<String> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        assertTrue(duplicates.isEmpty(), "Found duplicate item names: " + duplicates);
    }

    /**
     * Test that isItemThisType() correctly identifies ItemStacks by their NBT tags.
     *
     * <p>O2Items are identified by NBT tags, not by display name alone. This prevents players
     * from renaming vanilla items to match O2Item names. Verifies:</p>
     * <ul>
     * <li>Matching O2Items return true</li>
     * <li>Different O2Item types return false</li>
     * <li>Vanilla items renamed to match O2Item names return false (no NBT tag)</li>
     * <li>Enchanted O2Items are correctly identified</li>
     * </ul>
     */
    @Test
    void isItemThisTypeTest() {
        // test 1: same types
        O2ItemType expectedType = O2ItemType.GALLEON;
        ItemStack itemStack = new O2Item(testPlugin, expectedType).getItem(1);
        assertNotNull(itemStack);
        assertTrue(expectedType.isItemThisType(itemStack), "isItemThisType() did not match expected type");

        // test 2: different types
        itemStack = new O2Item(testPlugin, O2ItemType.KNUT).getItem(1);
        assertNotNull(itemStack);
        assertFalse(expectedType.isItemThisType(itemStack), "isItemThisType() matched on an item of a different type");

        // test 3: renamed item, such as with an anvil
        ItemStack renamedItemStack = new ItemStack(O2ItemType.GALLEON.getMaterial(), 1);
        ItemMeta itemMeta = renamedItemStack.getItemMeta();
        itemMeta.setDisplayName(O2ItemType.GALLEON.getName());
        renamedItemStack.setItemMeta(itemMeta);
        assertEquals(O2ItemType.GALLEON.getName(), renamedItemStack.getItemMeta().getDisplayName());
        assertFalse(expectedType.isItemThisType(renamedItemStack), "isItemThisType() matched on a basic item which was renamed");

        // test 4: enchanted items
        expectedType = O2ItemType.BROOMSTICK;
        itemStack = new O2Item(testPlugin, expectedType).getItem(1);
        assertNotNull(itemStack);
        assertTrue(expectedType.isItemThisType(itemStack), "isItemThisType() did not match expected type for enchanted item");
    }

    /**
     * Test that getItem() creates ItemStacks with correct metadata.
     *
     * <p>Verifies that getItem() creates properly configured ItemStacks with:</p>
     * <ul>
     * <li>Correct display name</li>
     * <li>Correct stack amount</li>
     * <li>NBT tags for item identification</li>
     * <li>Lore for items that have custom lore</li>
     * <li>Proper metadata for enchanted items</li>
     * <li>Hidden tooltip flag for potion items</li>
     * </ul>
     */
    @Test
    void getItemTest() {
        // test 1: simple item
        O2ItemType itemType = O2ItemType.DEW_DROP;
        int amount = 1;
        ItemStack itemStack = itemType.getItem(amount);
        assertNotNull(itemStack, "itemType.getItem(1) returned null");
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta);
        assertEquals(itemType.getName(), itemMeta.getDisplayName(), "ItemStack did not have expected name");
        assertEquals(amount, itemStack.getAmount(), "itemStack.getAmount() did not have expected amount");
        // check NBT tags
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        assertTrue(container.has(O2Items.o2ItemTypeKey), "Item does not have O2Items.o2ItemTypeKey set in NBT tags");

        // test 2: test different amount
        amount = 5;
        itemStack = itemType.getItem(amount);
        assertNotNull(itemStack);
        assertEquals(amount, itemStack.getAmount(), "itemStack.getAmount() did not have expected amount");

        // test 3: item with lore
        itemType = O2ItemType.INVISIBILITY_CLOAK;
        itemStack = itemType.getItem(1);
        assertNotNull(itemStack);
        itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta);
        assertEquals(itemType.getName(), itemMeta.getDisplayName(), "ItemStack did not have expected name");
        List<String> lore = itemMeta.getLore();
        assertNotNull(lore, "Lore for O2ItemType.INVISIBILITY_CLOAK was null");
        assertEquals(itemType.getLore(), lore.getFirst(), "ItemStack did not have expected lore");

        // test 4 enchanted item
        itemType = O2ItemType.BROOMSTICK;
        itemStack = itemType.getItem(1);
        assertNotNull(itemStack);
        itemMeta = itemStack.getItemMeta();
        assertEquals(itemType.getName(), itemMeta.getDisplayName(), "ItemStack did not have expected name");

        // test 5 potion item
        itemType = O2ItemType.SOPOPHORUS_BEAN_JUICE;
        itemStack = itemType.getItem(1);
        assertNotNull(itemStack);
        itemMeta = itemStack.getItemMeta();
        assertEquals(itemType.getName(), itemMeta.getDisplayName(), "ItemStack did not have expected name");
        assertTrue(itemMeta.hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP), "potion item did not have ItemFlag.HIDE_ADDITIONAL_TOOLTIP");
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
