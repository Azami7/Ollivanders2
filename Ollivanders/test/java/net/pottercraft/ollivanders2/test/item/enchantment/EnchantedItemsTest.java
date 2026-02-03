package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import net.pottercraft.ollivanders2.item.enchantment.EnchantedItems;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnchantedItemsTest {
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
     * The test world used for all enchantment tests.
     */
    static World testWorld;

    /**
     * The origin location used for dropping items in tests.
     */
    static Location origin;

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
        testWorld = mockServer.addSimpleWorld("world");
        origin = new Location(testWorld, 0, 4, 0);

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test onEnable initialization.
     * <p>
     * Basic initialization is verified by the plugin loading successfully in globalSetUp().
     * </p>
     */
    @Test
    void onEnableTest() {
        // basic initialization verified by plugin loading in globalSetUp()
    }

    /**
     * Test adding an enchantment to an Item (dropped item entity).
     * <p>
     * Also provides test coverage for addEnchantedItem(ItemStack, ItemEnchantmentType, int, String, String).
     * </p>
     */
    @Test
    void addEnchantedItemTest() {
        // enchantment without args
        Item item = testWorld.dropItem(origin, new ItemStack(Material.APPLE, 1));

        ItemEnchantmentType enchantmentType = ItemEnchantmentType.FLAGRANTE;
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, enchantmentType, 1, null);

        ItemStack itemStack = item.getItemStack();
        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "Enchantment not added to item");
        assertEquals(enchantmentType, enchantment.getType(), "enchantment not the correct type");
        assertEquals(item.getUniqueId().toString(), Ollivanders2API.getItems().enchantedItems.getEnchantmentID(itemStack), "Enchantment ID not set to item.getUniqueId().toString()");
    }

    /**
     * Test adding an enchantment with optional args to an Item.
     * <p>
     * Also provides test coverage for addEnchantedItem(ItemStack, ItemEnchantmentType, int, String, String).
     * </p>
     */
    @Test
    void addEnchantedItemWithArgsTest() {
        // enchantment with args
        Item item = testWorld.dropItem(origin, new ItemStack(Material.APPLE, 1));

        ItemEnchantmentType enchantmentType = ItemEnchantmentType.PORTUS;
        String args = "world 100 4 100";
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, enchantmentType, 1, args);

        ItemStack itemStack = item.getItemStack();
        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "Enchantment not added to item");
        assertEquals(args, enchantment.getArgs(), "Optional args not set on enchantment");
    }

    /**
     * Test adding an enchantment directly to an ItemStack with explicit enchantment ID.
     */
    @Test
    void addEnchantedItemItemStackTest() {
        ItemStack itemStack = new ItemStack(Material.NETHERITE_INGOT);
        ItemEnchantmentType enchantmentType = ItemEnchantmentType.CELATUM;
        int magnitude = 2;
        String eid = "addEnchantedItemItemStackTest1";
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, enchantmentType, magnitude, eid, "Some hidden text");

        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "Failed to add enchantment to itemStack");
        assertEquals(enchantmentType, enchantment.getType(), "Enchantment is the wrong type");
        assertEquals(magnitude, enchantment.getMagnitude(), "Enchantment magnitude not expected");
        assertEquals(eid, Ollivanders2API.getItems().enchantedItems.getEnchantmentID(itemStack), "Enchantment ID not expected");
    }

    /**
     * Test adding an enchantment to an O2Item ItemStack preserves the O2Item NBT tags.
     */
    @Test
    void addEnchantedItemItemStackO2ItemTest() {
        ItemStack itemStack = O2ItemType.KNUT.getItem(1);
        assertNotNull(itemStack, "O2ItemType.KNUT.getItem() returned null");

        ItemEnchantmentType enchantmentType = ItemEnchantmentType.CELATUM;
        String eid = "addEnchantedItemItemStackO2ItemTest1";
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, enchantmentType, 1, eid, "Some hidden text");

        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "Failed to add enchantment to O2Item itemStack");

        // make sure we did not mess up the O2Item NBT tags
        assertEquals(O2ItemType.KNUT, Ollivanders2API.getItems().getItemTypeByItemStack(itemStack), "O2Item type not preserved by addEnchantment");
    }

    /**
     * Test isEnchanted with Item (dropped item entity).
     * <p>
     * Also provides test coverage for isEnchanted(ItemStack).
     * </p>
     */
    @Test
    void isEnchantedTest() {
        Item item = testWorld.dropItem(origin, new ItemStack(Material.WOODEN_AXE, 1));
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(item), "isEnchanted(item) returned true for normal item.");

        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.FLAGRANTE, 1, null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(item), "isEnchanted(item) returned false for enchanted item.");
    }

    /**
     * Test isEnchanted with ItemStack including O2Items.
     */
    @Test
    void isEnchantedItemStackTest() {
        // not enchanted
        ItemStack itemStack = new ItemStack(Material.ALLIUM, 1);
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack), "isEnchanted(itemStack) returned true for normal item stack");

        // enchanted
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.FLAGRANTE, 1, "isEnchantedItemStackTest1", null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack), "isEnchanted(itemStack) returned false for an enchanted item stack");

        // not enchanted o2item
        itemStack = O2ItemType.PHOENIX_FEATHER.getItem(1);
        assertNotNull(itemStack, "O2ItemType.PHOENIX_FEATHER.getItem() returned null");
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack), "isEnchanted(itemStack) returned true for normal o2item");

        // enchanted o2item (broomstick has VOLATUS enchantment)
        itemStack = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(itemStack, "O2ItemType.BROOMSTICK.getItem() returned null");
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack), "isEnchanted(itemStack) returned false for an enchanted o2item");
    }

    /**
     * Test isCursed detects cursed enchantments (FLAGRANTE, GEMINO) but not beneficial ones (PORTUS).
     * <p>
     * Also provides test coverage for isCursed(ItemStack).
     * </p>
     */
    @Test
    void isCursedTest() {
        // not enchanted item
        Item item = testWorld.dropItem(origin, new ItemStack(Material.NETHERITE_INGOT, 1));
        assertFalse(Ollivanders2API.getItems().enchantedItems.isCursed(item), "isCursed(item) returned true for a normal item");

        // not cursed item
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.PORTUS, 1, "world 20 4 20");
        assertFalse(Ollivanders2API.getItems().enchantedItems.isCursed(item), "isCursed(item) returned true for a non-curse enchantment");

        // cursed item
        item = testWorld.dropItem(origin, new ItemStack(Material.BONE, 1));
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.FLAGRANTE, 1, null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isCursed(item), "isCursed(item) returned false for Flagrante curse");

        item = testWorld.dropItem(origin, new ItemStack(Material.BELL, 1));
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.GEMINO, 1, null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isCursed(item), "isCursed(item) returned false for Gemino curse");
    }

    /**
     * Test isCursed with ItemStack.
     * <p>
     * Core functionality is covered by isCursedTest() which tests both Item and ItemStack overloads.
     * </p>
     */
    @Test
    void isCursedItemStackTest() {
        // core functionality covered by isCursedTest()
    }

    /**
     * Test isCursedLevelBased detects curses based on magic level.
     * <p>
     * Detection succeeds if: curse_level <= detection_level + 1.
     * FLAGRANTE is an EXPERT level curse, so it should not be detected at BEGINNER level
     * but should be detected at NEWT level.
     * </p>
     */
    @Test
    void isCursedLevelBasedTest() {
        // normal item - should return false
        Item item = testWorld.dropItem(origin, new ItemStack(Material.WRITTEN_BOOK, 1));
        assertFalse(Ollivanders2API.getItems().enchantedItems.isCursedLevelBased(item, MagicLevel.BEGINNER), "Normal item should not be detected as cursed");

        // FLAGRANTE is EXPERT level curse - detection depends on magic level
        item = testWorld.dropItem(origin, new ItemStack(Material.BOW, 1));
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.FLAGRANTE, 1, null);
        assertFalse(Ollivanders2API.getItems().enchantedItems.isCursedLevelBased(item, MagicLevel.BEGINNER), "Should not detect expert level curse at MagicLevel.BEGINNER");
        assertTrue(Ollivanders2API.getItems().enchantedItems.isCursedLevelBased(item, MagicLevel.NEWT), "Should detect expert level curse at MagicLevel.NEWT");
    }

    /**
     * Test getEnchantmentTypeKey returns the enchantment type name string.
     */
    @Test
    void getEnchantmentTypeKeyTest() {
        // normal item
        ItemStack itemStack = new ItemStack(Material.STICK, 1);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(itemStack), "getEnchantmentTypeKey(itemStack) for normal item did not return null");

        // enchanted item
        ItemEnchantmentType enchantmentType = ItemEnchantmentType.VOLATUS;
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, enchantmentType, 1, "getEnchantmentTypeKeyTest1", null);
        String enchantmentTypeKey = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(itemStack);
        assertNotNull(enchantmentTypeKey, "getEnchantmentTypeKey(itemStack) returned null for enchanted item");
        assertTrue(enchantmentType.getName().equalsIgnoreCase(enchantmentTypeKey), "getEnchantmentTypeKey(itemStack) did not return expected enchantment type, expected: " + enchantmentType.getName() + " actual: " + enchantmentTypeKey);

        // o2item (not enchanted)
        itemStack = O2ItemType.GALLEON.getItem(1);
        assertNotNull(itemStack, "O2ItemType.GALLEON.getItem() returned null");
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(itemStack), "getEnchantmentTypeKey(itemStack) for o2item did not return null");

        // enchanted o2item (broomstick has VOLATUS enchantment)
        itemStack = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(itemStack, "O2ItemType.BROOMSTICK.getItem() returned null");
        enchantmentTypeKey = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(itemStack);
        assertNotNull(enchantmentTypeKey, "getEnchantmentTypeKey(itemStack) returned null for enchanted o2item");
        assertTrue(enchantmentType.getName().equalsIgnoreCase(enchantmentTypeKey), "getEnchantmentTypeKey(itemStack) did not return expected enchantment type, expected: " + enchantmentType.getName() + " actual: " + enchantmentTypeKey);
    }

    /**
     * Test getEnchantmentType returns the ItemEnchantmentType enum value.
     */
    @Test
    void getEnchantmentTypeTest() {
        // normal item
        ItemStack itemStack = new ItemStack(Material.STICK, 1);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentType(itemStack), "getEnchantmentType(itemStack) for normal item did not return null");

        // enchanted item
        ItemEnchantmentType expectedEnchantmentType = ItemEnchantmentType.VOLATUS;
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, expectedEnchantmentType, 1, "getEnchantmentTypeTest1", null);
        ItemEnchantmentType enchantmentType = Ollivanders2API.getItems().enchantedItems.getEnchantmentType(itemStack);
        assertNotNull(enchantmentType, "getEnchantmentType(itemStack) returned null for enchanted item");
        assertEquals(expectedEnchantmentType, enchantmentType, "getEnchantmentType(itemStack) did not return expected enchantment type");
    }

    /**
     * Test getEnchantmentID returns the unique enchantment identifier.
     * <p>
     * For Item entities, the ID defaults to the item's UUID. For ItemStacks, the ID is explicitly set.
     * </p>
     */
    @Test
    void getEnchantmentIDTest() {
        // normal item - should return null
        Item item = testWorld.dropItem(origin, new ItemStack(Material.WRITTEN_BOOK, 1));
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentID(item), "getEnchantmentID(item) did not return null for normal item");

        // enchanted item - ID defaults to item UUID
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.CELATUM, 1, "some hidden text");
        String eid = Ollivanders2API.getItems().enchantedItems.getEnchantmentID(item);
        assertNotNull(eid, "getEnchantmentID(item) returned null for enchanted item");
        assertEquals(item.getUniqueId().toString(), eid, "getEnchantmentID(item) did not return item uuid");

        // enchanted itemStack - ID is explicitly set
        String expectedID = "getEnchantmentIDTest1";
        ItemStack itemStack = new ItemStack(Material.COMPASS, 1);
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.FLAGRANTE, 1, expectedID, null);
        eid = Ollivanders2API.getItems().enchantedItems.getEnchantmentID(itemStack);
        assertNotNull(eid, "getEnchantmentID(itemStack) returned null for enchanted itemStack");
        assertEquals(expectedID, eid, "getEnchantmentID(itemStack) did not return expected id");
    }

    /**
     * Test getEnchantmentMagnitude returns the correct magnitude value.
     */
    @Test
    void getEnchantmentMagnitudeTest() {
        // normal item
        ItemStack itemStack = new ItemStack(Material.FEATHER, 1);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentMagnitude(itemStack), "getEnchantmentMagnitude(itemStack) did not return null for normal item");

        // enchanted item with magnitude 1
        int expectedMagnitude = 1;
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.GEMINO, expectedMagnitude, "getEnchantmentMagnitudeTest1", null);
        Integer magnitude = Ollivanders2API.getItems().enchantedItems.getEnchantmentMagnitude(itemStack);
        assertNotNull(magnitude, "getEnchantmentMagnitude returned null for enchanted item");
        assertEquals(expectedMagnitude, magnitude, "getEnchantmentMagnitude did not return expected magnitude");

        // enchanted item with magnitude 3
        itemStack = new ItemStack(Material.GLASS_BOTTLE, 1);
        expectedMagnitude = 3;
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.GEMINO, expectedMagnitude, "getEnchantmentMagnitudeTest2", null);
        magnitude = Ollivanders2API.getItems().enchantedItems.getEnchantmentMagnitude(itemStack);
        assertNotNull(magnitude, "getEnchantmentMagnitude returned null for enchanted item with magnitude 3");
        assertEquals(expectedMagnitude, magnitude, "getEnchantmentMagnitude did not return expected magnitude 3");
    }

    /**
     * Test getEnchantmentArgs returns the optional args string.
     */
    @Test
    void getEnchantmentArgsTest() {
        // normal item
        ItemStack itemStack = new ItemStack(Material.HONEY_BOTTLE, 1);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentArgs(itemStack), "getEnchantmentArgs(itemStack) did not return null for normal item");

        // enchanted item with null args
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.FLAGRANTE, 1, "getEnchantmentArgsTest1", null);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentArgs(itemStack), "getEnchantmentArgs(itemStack) did not return null for enchanted item with null args");

        // enchanted item with args
        String expectedArgs = "some hidden text";
        itemStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.CELATUM, 1, "getEnchantmentArgsTest2", expectedArgs);
        String args = Ollivanders2API.getItems().enchantedItems.getEnchantmentArgs(itemStack);
        assertNotNull(args, "getEnchantmentArgs returned null for enchanted item with args");
        assertEquals(expectedArgs, args, "getEnchantmentArgs did not return expected args");
    }

    /**
     * Test getEnchantment returns the Enchantment object for enchanted items.
     */
    @Test
    void getEnchantmentTest() {
        // normal itemStack
        ItemStack itemStack = new ItemStack(Material.INK_SAC, 1);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack), "getEnchantment returned non-null for normal itemStack");

        // normal item
        Item item = testWorld.dropItem(origin, itemStack);
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantment(item.getItemStack()), "getEnchantment returned non-null for normal item");

        // enchanted itemStack
        ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINO;
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, enchantmentType, 1, "getEnchantmentTest1", null);
        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "getEnchantment returned null for enchanted itemStack");
        assertEquals(enchantmentType, enchantment.getType(), "getEnchantment returned wrong enchantment type for itemStack");

        // enchanted item
        itemStack = new ItemStack(Material.JACK_O_LANTERN, 1);
        item = testWorld.dropItem(origin, itemStack);
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, enchantmentType, 1, null);
        enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(item.getItemStack());
        assertNotNull(enchantment, "getEnchantment returned null for enchanted item");
        assertEquals(enchantmentType, enchantment.getType(), "getEnchantment returned wrong enchantment type for item");

        // o2item (not enchanted)
        itemStack = O2ItemType.KELPIE_HAIR.getItem(1);
        assertNotNull(itemStack, "O2ItemType.KELPIE_HAIR.getItem() returned null");
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack), "getEnchantment returned non-null for non-enchanted o2item");

        // enchanted o2item (broomstick has VOLATUS enchantment)
        itemStack = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(itemStack, "O2ItemType.BROOMSTICK.getItem() returned null");
        enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "getEnchantment returned null for enchanted o2item");
        assertEquals(O2ItemType.BROOMSTICK.getItemEnchantment(), enchantment.getType(), "getEnchantment returned wrong enchantment type for broomstick");
    }

    /**
     * Test createEnchantment creates Enchantment objects from type name strings.
     */
    @Test
    void createEnchantmentTest() {
        // enchantment with args
        ItemEnchantmentType enchantmentType = ItemEnchantmentType.CELATUM;
        int magnitude = 1;
        String args = "some hidden text";
        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.createEnchantment(enchantmentType.toString(), magnitude, args);
        assertNotNull(enchantment, "createEnchantment returned null for CELATUM");
        assertEquals(enchantmentType, enchantment.getType(), "createEnchantment returned wrong type");
        assertEquals(magnitude, enchantment.getMagnitude(), "createEnchantment returned wrong magnitude");
        assertEquals(args, enchantment.getArgs(), "createEnchantment returned wrong args");

        // enchantment without args
        enchantmentType = ItemEnchantmentType.GEMINO;
        magnitude = 3;
        enchantment = Ollivanders2API.getItems().enchantedItems.createEnchantment(enchantmentType.toString(), magnitude, null);
        assertNotNull(enchantment, "createEnchantment returned null for GEMINO");
        assertEquals(enchantmentType, enchantment.getType(), "createEnchantment returned wrong type for GEMINO");
        assertEquals(magnitude, enchantment.getMagnitude(), "createEnchantment returned wrong magnitude for GEMINO");
        assertNull(enchantment.getArgs(), "createEnchantment should return null args for GEMINO");
    }

    /**
     * Test removeEnchantment removes enchantment from Item and cache.
     * <p>
     * Also provides test coverage for removeEnchantment(ItemStack).
     * </p>
     */
    @Test
    void removeEnchantmentTest() {
        Ollivanders2.debug = true;

        // enchanted item
        Item item = testWorld.dropItem(origin, new ItemStack(Material.MAGENTA_CANDLE, 1));
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.GEMINO, 1, null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(item), "Item should be enchanted after addEnchantedItem");
        item.setItemStack(Ollivanders2API.getItems().enchantedItems.removeEnchantment(item));
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(item), "Item should not be enchanted after removeEnchantment");

        // o2item with enchantments - verify O2Item NBT tags are preserved
        O2ItemType expectedItemType = O2ItemType.RUNESPOOR_EGG;
        ItemStack itemStack = expectedItemType.getItem(1);
        assertNotNull(itemStack, "O2ItemType.RUNESPOOR_EGG.getItem() returned null");
        item = testWorld.dropItem(origin, itemStack);
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(item, ItemEnchantmentType.GEMINO, 1, null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(item), "O2Item should be enchanted after addEnchantedItem");
        item.setItemStack(Ollivanders2API.getItems().enchantedItems.removeEnchantment(item));
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(item), "O2Item should not be enchanted after removeEnchantment");
        // make sure O2Item NBT tags are not affected
        O2ItemType itemType = Ollivanders2API.getItems().getItemTypeByItemStack(item.getItemStack());
        assertEquals(expectedItemType, itemType, "O2Item type should be preserved after removeEnchantment");
    }

    /**
     * Test removeEnchantment removes enchantment from ItemStack and cache.
     */
    @Test
    void removeEnchantmentItemStack() {
        ItemStack itemStack = new ItemStack(Material.NAME_TAG, 1);
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, ItemEnchantmentType.GEMINO, 1, "removeEnchantmentItemStack1", null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack), "ItemStack should be enchanted after addEnchantedItem");

        itemStack = Ollivanders2API.getItems().enchantedItems.removeEnchantment(itemStack);
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack), "ItemStack should not be enchanted after removeEnchantment");
    }

    /**
     * Test that removeEnchantmentNBT removes NBT tags without affecting the cache.
     * <p>
     * Verifies that:
     * <ul>
     * <li>The returned ItemStack has no enchantment NBT tags (not detected as enchanted)</li>
     * <li>The original ItemStack remains enchanted (method returns a clone)</li>
     * <li>The enchantment cache is not affected (enchantment still retrievable by ID)</li>
     * </ul>
     * </p>
     */
    @Test
    void removeEnchantmentNBTTest() {
        // Create and enchant an item
        ItemStack enchantedItemStack = new ItemStack(Material.GOLDEN_APPLE, 1);
        String eid = "removeEnchantmentNBTTest1";
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(enchantedItemStack, ItemEnchantmentType.FLAGRANTE, 1, eid, null);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(enchantedItemStack), "ItemStack should be enchanted before removeEnchantmentNBT");

        // Get the enchantment from cache to verify it exists
        Enchantment enchantmentBeforeRemoval = Ollivanders2API.getItems().enchantedItems.getEnchantment(enchantedItemStack);
        assertNotNull(enchantmentBeforeRemoval, "Enchantment should exist in cache before removeEnchantmentNBT");

        // Remove NBT tags (does not affect cache)
        ItemStack disenchantedItemStack = EnchantedItems.removeEnchantmentNBT(enchantedItemStack);

        // Verify the returned ItemStack is NOT enchanted (NBT tags removed)
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(disenchantedItemStack), "Returned ItemStack should not be enchanted after removeEnchantmentNBT");
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentType(disenchantedItemStack), "Returned ItemStack should have no enchantment type");
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentID(disenchantedItemStack), "Returned ItemStack should have no enchantment ID");
        assertNull(Ollivanders2API.getItems().enchantedItems.getEnchantmentMagnitude(disenchantedItemStack), "Returned ItemStack should have no enchantment magnitude");

        // Verify the original ItemStack is still enchanted (method clones the item)
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(enchantedItemStack), "Original ItemStack should still be enchanted after removeEnchantmentNBT");

        // Verify the cache still contains the enchantment (removeEnchantmentNBT does not affect cache)
        Enchantment cachedEnchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(enchantedItemStack);
        assertNotNull(cachedEnchantment, "Enchantment should still be in cache after removeEnchantmentNBT");
        assertEquals(enchantmentBeforeRemoval.getType(), cachedEnchantment.getType(), "Cached enchantment should have same type");

        // Verify the returned ItemStack is a different object (clone)
        assertNotSame(enchantedItemStack, disenchantedItemStack, "removeEnchantmentNBT should return a clone, not the original");
    }

    /**
     * Test onItemDespawn event handling.
     * <p>
     * Test coverage provided by the specific enchantment tests (FlagranteTest, GeminoTest, etc.).
     * </p>
     */
    @Test
    void onItemDespawnTest() {
        // test coverage provided by the specific enchantment tests
    }

    /**
     * Test onEntityPickupItem event handling.
     * <p>
     * Test coverage provided by the specific enchantment tests (FlagranteTest, GeminoTest, PortusTest, etc.).
     * </p>
     */
    @Test
    void onEntityPickupItemTest() {
        // test coverage provided by the specific enchantment tests
    }

    /**
     * Test onInventoryPickupItem event handling (hopper pickup).
     * <p>
     * Test coverage provided by the specific enchantment tests.
     * </p>
     */
    @Test
    void onInventoryItemPickupTest() {
        // test coverage provided by the specific enchantment tests
    }

    /**
     * Test onItemDrop event handling.
     * <p>
     * Test coverage provided by the specific enchantment tests.
     * </p>
     */
    @Test
    void onItemDropTest() {
        // test coverage provided by the specific enchantment tests
    }

    /**
     * Test onItemHeld event handling.
     * <p>
     * Test coverage provided by the specific enchantment tests.
     * </p>
     */
    @Test
    void onItemHeldTest() {
        // test coverage provided by the specific enchantment tests
    }

    /**
     * Test areBroomsEnabled configuration getter.
     * <p>
     * Basic getter method, configuration testing covered elsewhere.
     * </p>
     */
    @Test
    void areBroomsEnabledTest() {
        // basic getter, configuration testing covered elsewhere
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
