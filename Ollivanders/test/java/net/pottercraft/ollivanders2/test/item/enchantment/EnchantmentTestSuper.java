package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for {@link Enchantment} tests. Subclasses set {@link #enchantmentType} and {@link #itemType} in
 * {@link #setUp()} and implement the abstract event-handler tests; shared coverage of the accessors,
 * {@link #isHoldingEnchantedItem(Player)}, despawn, and inventory-pickup handling lives here.
 */
public abstract class EnchantmentTestSuper {
    /**
     * Shared MockBukkit server, mocked once per class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once with the default test config.
     */
    static Ollivanders2 testPlugin;

    /**
     * The test world enchanted items are dropped into.
     */
    static World testWorld;

    /**
     * The enchantment type under test; set by the subclass {@link #setUp()}.
     */
    ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINO;

    /**
     * The material for test items; set by the subclass {@link #setUp()} to one appropriate for the enchantment.
     */
    Material itemType = Material.APPLE;

    /**
     * Default enchantment args for the shared tests; subclasses may override for enchantments that require args.
     */
    String defaultArgs = null;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Configure {@link #enchantmentType} and {@link #itemType} for the enchantment under test.
     */
    @BeforeEach
    abstract void setUp();

    /**
     * {@link Enchantment#getName()} is a trivial getter; no assertions needed.
     */
    @Test
    void getNameTest() {
    }

    /**
     * {@link Enchantment#getMagnitude()} is a trivial getter; no assertions needed.
     */
    @Test
    void getMagnitudeTest() {
    }

    /**
     * {@link Enchantment#getType()} is a trivial getter; no assertions needed.
     */
    @Test
    void getTypeTest() {
    }

    /**
     * {@link Enchantment#getArgs()} is a trivial getter; no assertions needed.
     */
    @Test
    void getArgsTest() {
    }

    /**
     * Apply {@link #enchantmentType} to the given item at the given magnitude and return the resulting enchantment,
     * asserting it was applied.
     *
     * @param itemStack the ItemStack to enchant
     * @param magnitude the power level of the enchantment
     * @param args      optional enchantment-specific arguments
     * @return the applied enchantment
     */
    @NotNull
    Enchantment addEnchantment(@NotNull ItemStack itemStack, int magnitude, @Nullable String args) {
        String eid = java.util.UUID.randomUUID().toString();
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, enchantmentType, magnitude, eid, args);

        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "Failed to add enchantment " + enchantmentType.getName());

        return enchantment;
    }

    /**
     * Create a new {@link #itemType} ItemStack with {@link #enchantmentType} applied at the given magnitude.
     *
     * @param magnitude the power level of the enchantment
     * @param args      optional enchantment-specific arguments
     * @return the enchanted ItemStack
     */
    @NotNull
    ItemStack makeEnchantedItem(int magnitude, @Nullable String args) {
        ItemStack itemStack = new ItemStack(itemType, 1);
        addEnchantment(itemStack, magnitude, args);

        assertNotNull(itemStack);

        return itemStack;
    }

    /**
     * {@link Enchantment#isHoldingEnchantedItem(Player)} returns true iff a cursed item is in either hand, across all
     * main-hand/off-hand combinations.
     */
    @Test
    void isHoldingEnchantedItemTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack notEnchantedItem = new ItemStack(itemType, 1);
        ItemStack enchantedItem = new ItemStack(itemType, 1);

        Enchantment enchantment = addEnchantment(enchantedItem, 1, null);

        // false when holding non-enchanted item in main hand
        player.getInventory().setItemInMainHand(notEnchantedItem);
        assertFalse(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned true for normal items.");
        player.getInventory().setItemInMainHand(null);

        // false when holding non-enchanted item in off-hand
        player.getInventory().setItemInOffHand(notEnchantedItem);
        assertFalse(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned true for normal items.");
        player.getInventory().setItemInOffHand(null);

        // true when holding enchanted item in main hand
        player.getInventory().setItemInMainHand(enchantedItem);
        assertTrue(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned false when enchanted item in main hand");
        player.getInventory().setItemInMainHand(null);

        // true when holding enchanted item in off-hand
        player.getInventory().setItemInOffHand(enchantedItem);
        assertTrue(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned false when enchanted item in off hand");
        player.getInventory().setItemInOffHand(null);

        // true when holding non-enchanted item in main hand and enchanted item in off-hand
        player.getInventory().setItemInMainHand(notEnchantedItem);
        player.getInventory().setItemInOffHand(enchantedItem);
        assertTrue(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned false when non-enchanted item in main hand and enchanted item in off hand");

        // true when holding enchanted item in main hand and non-enchanted item in off-hand
        player.getInventory().setItemInOffHand(notEnchantedItem);
        player.getInventory().setItemInMainHand(enchantedItem);
        assertTrue(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned false when enchanted item in main hand and non-enchanted item in off hand");
    }

    /**
     * An enchanted item's despawn is cancelled, keeping the cursed item in the world.
     */
    @Test
    void doItemDespawnTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack enchantedItem = makeEnchantedItem(1, defaultArgs);

        Item item = player.getWorld().dropItem(player.getLocation(), enchantedItem);
        ItemDespawnEvent event = new ItemDespawnEvent(item, item.getLocation());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(event.isCancelled(), "doItemDespawn should cancel the despawn event for " + enchantmentType.getName() + " items");
    }

    /**
     * Verify the enchantment's behavior when a player picks up a cursed item.
     */
    @Test
    abstract void doEntityPickupItemTest();

    /**
     * A block inventory's (e.g. hopper's) attempt to collect a cursed item is cancelled.
     */
    @Test
    void doInventoryPickupItemTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack enchantedItem = makeEnchantedItem(1, defaultArgs);

        Item item = player.getWorld().dropItem(player.getLocation(), enchantedItem);
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER);
        InventoryPickupItemEvent event = new InventoryPickupItemEvent(inventory, item);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(event.isCancelled(), "doInventoryPickupItem should cancel the inventory pickup event for " + enchantmentType.getName() + " items");
    }

    /**
     * Verify the enchantment's behavior when a player drops a cursed item.
     */
    @Test
    abstract void doItemDropTest();

    /**
     * Verify the enchantment's behavior when a player changes which item slot is held.
     */
    @Test
    abstract void doItemHeldTest();

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
