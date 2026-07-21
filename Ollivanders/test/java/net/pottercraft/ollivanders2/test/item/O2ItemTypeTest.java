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
 * Unit tests for {@link O2ItemType}.
 */
public class O2ItemTypeTest {
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

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * getName() returns the item's display name, whether custom, material-matching, or enchanted.
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
     * getLore() returns the custom lore, or the item name when there is no lore.
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
     * getMaterial() returns the item's backing Bukkit material.
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
     * getItemEnchantment() returns the item's enchantment, or null when it has none.
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
     * setMaterial() changes an item type's material at runtime (restored afterward to isolate other tests).
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
     * getTypeByName() finds item types by display name, case-insensitively, and returns null for an unknown name.
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
     * Every item has a unique name, so lookups like getTypeByName() are unambiguous.
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
     * isItemThisType() matches by NBT tag, not display name, so a renamed vanilla item does not match.
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
     * getItem() builds an ItemStack with the right name, amount, NBT type tag, lore, and (for potions) hidden tooltip.
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

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
