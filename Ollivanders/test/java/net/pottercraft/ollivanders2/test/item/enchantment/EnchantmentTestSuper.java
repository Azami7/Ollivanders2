package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
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
 * Abstract base class for item enchantment test suites.
 * <p>
 * Provides a template for testing concrete enchantment implementations via the Template Method pattern.
 * Each concrete enchantment test class (e.g., VolatusTest, FlagranteTest) extends this class and sets
 * the {@link #enchantmentType} and {@link #itemType}, then implements abstract test methods for the
 * enchantment's event handlers.
 * </p>
 * <p>
 * This superclass handles:
 * <ul>
 * <li>Mock Bukkit server setup and teardown (one-time for all tests)</li>
 * <li>Plugin initialization with default test configuration</li>
 * <li>Tests for simple accessors (getName, getMagnitude, getType, getArgs)</li>
 * <li>Comprehensive tests for isHoldingEnchantedItem() across all inventory slot combinations</li>
 * <li>Template methods for testing event handlers (doItemDespawn, doEntityPickupItem, etc.)</li>
 * <li>Cleanup of test state after each test method</li>
 * </ul>
 * </p>
 * <p>
 * Subclasses must:
 * <ol>
 * <li>Implement {@link #setUp()} to configure {@link #enchantmentType} and {@link #itemType}</li>
 * <li>Implement all five abstract event handler test methods</li>
 * </ol>
 * </p>
 */
public abstract class EnchantmentTestSuper {
    /**
     * Shared mock Bukkit server instance for all tests.
     * <p>
     * Static field initialized once before all tests in this class hierarchy. Reused across all test
     * methods and instances to avoid expensive server setup/teardown overhead. All concrete test classes
     * that extend this superclass share the same server instance.
     * </p>
     */
    static ServerMock mockServer;

    /**
     * The Ollivanders2 plugin instance loaded with test configuration.
     * <p>
     * Initialized once before all tests in this class hierarchy with the default test config
     * (Ollivanders/test/resources/default_config.yml). Provides access to plugin functionality,
     * the Bukkit scheduler, and the Ollivanders2 API during tests. The plugin is started with the
     * server startup delay (20 ticks) performed to allow proper initialization.
     * </p>
     */
    static Ollivanders2 testPlugin;

    /**
     * The test world used for all portkey teleportation tests
     */
    static World testWorld;

    /**
     * The enchantment type being tested by this test class instance.
     * <p>
     * Set by the concrete test class's {@link #setUp()} method. Determines which enchantment
     * implementation is tested and is used by helper methods like {@link #addEnchantment(ItemStack, int, String)}
     * to create enchanted items of the correct type.
     * </p>
     */
    ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINO;

    /**
     * The Bukkit material type used for creating test items in this test class instance.
     * <p>
     * Set by the concrete test class's {@link #setUp()} method. Should match a material appropriate
     * for the enchantment being tested (e.g., Material.STICK for VOLATUS/VOLATUS enchantments on brooms).
     * Used to create ItemStacks for enchantment application in test methods.
     * </p>
     */
    Material itemType = Material.APPLE;

    /**
     *
     */
    String defaultArgs = null;

    /**
     * Global setup to initialize the mock Bukkit server and plugin before all tests.
     * <p>
     * Called once before all tests in this class hierarchy by JUnit. Creates the shared MockBukkit
     * server instance and loads the Ollivanders2 plugin with default test configuration. Advances the
     * server scheduler by the startup delay (20 ticks) to allow the plugin to fully initialize. This
     * shared server instance is reused across all concrete test classes and methods to minimize expensive
     * server setup/teardown overhead.
     * </p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Per-test setup hook for subclasses to configure the enchantment being tested.
     * <p>
     * Called before each test method by JUnit. Subclasses must implement this to set:
     * <ul>
     * <li>{@link #enchantmentType} - the ItemEnchantmentType to test</li>
     * <li>{@link #itemType} - the Bukkit Material for test items</li>
     * </ul>
     * </p>
     */
    @BeforeEach
    abstract void setUp();

    /**
     * Test {@link Enchantment#getName()}.
     * <p>
     * This is a simple accessor method that returns a static value from the enchantment type.
     * No explicit test is needed as the method's correctness is trivial.
     * </p>
     */
    @Test
    void getNameTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link Enchantment#getMagnitude()}.
     * <p>
     * This is a simple accessor method that returns the magnitude passed to the constructor.
     * No explicit test is needed as the method's correctness is trivial.
     * </p>
     */
    @Test
    void getMagnitudeTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link Enchantment#getType()}.
     * <p>
     * This is a simple accessor method that returns the enchantment type.
     * No explicit test is needed as the method's correctness is trivial.
     * </p>
     */
    @Test
    void getTypeTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Test {@link Enchantment#getArgs()}.
     * <p>
     * This is a simple accessor method that returns the optional arguments passed to the constructor.
     * No explicit test is needed as the method's correctness is trivial.
     * </p>
     */
    @Test
    void getArgsTest() {
        // simple getter, so we can skip this test
    }

    /**
     * Helper method to apply an enchantment to an ItemStack and retrieve the Enchantment object.
     * <p>
     * Creates a random enchantment ID, applies the currently-configured {@link #enchantmentType} to
     * the given ItemStack with the specified magnitude and arguments, then retrieves and returns the
     * Enchantment object. Asserts that the enchantment was successfully applied.
     * </p>
     *
     * @param itemStack the ItemStack to enchant
     * @param magnitude the power level of the enchantment
     * @param args      optional enchantment-specific arguments, or null
     * @return the Enchantment object for the applied enchantment
     * @throws AssertionError if the enchantment could not be applied or retrieved
     */
    @NotNull
    Enchantment addEnchantment(@NotNull ItemStack itemStack, int magnitude, @Nullable String args) {
        String eid = Integer.toString(Ollivanders2Common.random.nextInt());
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(itemStack, enchantmentType, magnitude, eid, args);

        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
        assertNotNull(enchantment, "Failed to add enchantment " + enchantmentType.getName());

        return enchantment;
    }

    /**
     * Helper method to create a new ItemStack with the enchantment applied.
     * <p>
     * Convenience method that creates a new ItemStack of the currently-configured {@link #itemType}
     * and applies the enchantment via {@link #addEnchantment(ItemStack, int, String)} in a single call.
     * This is useful for tests that need enchanted items without performing the setup in multiple steps.
     * </p>
     *
     * @param magnitude the power level of the enchantment
     * @param args      optional enchantment-specific arguments, or null
     * @return a new ItemStack with the enchantment applied
     */
    @NotNull
    ItemStack makeEnchantedItem(int magnitude, @Nullable String args) {
        ItemStack itemStack = new ItemStack(itemType, 1);
        addEnchantment(itemStack, magnitude, args);

        assertNotNull(itemStack);

        return itemStack;
    }

    /**
     * Test {@link Enchantment#isHoldingEnchantedItem(Player)} across all inventory scenarios.
     * <p>
     * Verifies that the method correctly detects whether a player is holding an item enchanted with the
     * currently-configured enchantment type, checking both main hand and off-hand slots.
     * </p>
     * <p>
     * Test cases verified:
     * <ul>
     * <li>Non-enchanted item in main hand → returns false</li>
     * <li>Non-enchanted item in off-hand → returns false</li>
     * <li>Enchanted item in main hand only → returns true</li>
     * <li>Enchanted item in off-hand only → returns true</li>
     * <li>Non-enchanted item in main hand, enchanted in off-hand → returns true</li>
     * <li>Enchanted item in main hand, non-enchanted in off-hand → returns true</li>
     * </ul>
     * </p>
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
     * Test the doItemDespawn event handler of the enchantment being tested.
     * <p>
     * Subclasses must implement this to verify the enchantment's behavior when an enchanted item
     * despawns from the world (is removed due to inactivity timeout).
     * </p>
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
     * Test the doEntityPickupItem event handler of the enchantment being tested.
     * <p>
     * Subclasses must implement this to verify the enchantment's behavior when a player picks up an
     * item enchanted with this enchantment type.
     * </p>
     */
    @Test
    abstract void doEntityPickupItemTest();

    /**
     * Test the doInventoryPickupItem event handler.
     * <p>
     * Verifies that when a hopper or other block-based inventory attempts to pick up the enchanted item,
     * the pickup event is cancelled. This prevents automated item collection systems from picking up dangerous
     * cursed items.
     * </p>
     * <p>
     * Setup: A hopper inventory is created and an InventoryPickupItemEvent is fired for an enchanted item.
     * Expected: The InventoryPickupItemEvent is cancelled, preventing the hopper from taking the item.
     * </p>
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
     * Test the doItemDrop event handler of the enchantment being tested.
     * <p>
     * Subclasses must implement this to verify the enchantment's behavior when a player drops an item
     * enchanted with this enchantment type.
     * </p>
     */
    @Test
    abstract void doItemDropTest();

    /**
     * Test the doItemHeld event handler of the enchantment being tested.
     * <p>
     * Subclasses must implement this to verify the enchantment's behavior when a player changes which
     * item slot is held in their hand (e.g., switching from slot 1 to slot 2 in the hotbar).
     * </p>
     */
    @Test
    abstract void doItemHeldTest();

    /**
     * Per-test cleanup to reset the shared test state.
     * <p>
     * Called after each test method by JUnit. Resets the global debug flag to false to prevent
     * debug output from one test affecting the logs or behavior of subsequent tests in the suite.
     * </p>
     */
    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    /**
     * Global cleanup to release MockBukkit resources after all tests complete.
     * <p>
     * Called once after all tests in this class hierarchy have finished by JUnit. Unmocks the
     * MockBukkit server instance and releases all server resources to prevent memory leaks and
     * ensure clean test execution for subsequent test classes in the suite.
     * </p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
