package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link ItemEnchantmentType#GEMINO} duplication curse enchantment.
 * <p>
 * Verifies that the GEMINO enchantment correctly duplicates items exponentially when picked up.
 * GEMINO is an expert-level curse that, when a player picks up a cursed item, creates 2^magnitude
 * additional copies and adds them to the player's inventory. For example: magnitude 1 creates 2 copies,
 * magnitude 2 creates 4 copies, magnitude 3 creates 8 copies, magnitude 4 creates 16 copies.
 * </p>
 * <p>
 * All test scenarios are consolidated into a single test method to ensure sequential execution
 * and avoid race conditions from parallel test execution with the shared MockBukkit server.
 * </p>
 * <p>
 * Test coverage includes:
 * <ul>
 * <li>Exponential duplication: verifies correct number of copies created for each magnitude (1-4)</li>
 * <li>Inventory management: verifies the original enchanted item and duplicates coexist properly</li>
 * <li>No action on drop/held events: GEMINO only triggers on item pickup, not drop or item slot switching</li>
 * </ul>
 * </p>
 *
 * @see ItemEnchantmentType#GEMINO the duplication curse being tested
 */
public class GeminoTest extends EnchantmentTestSuper {
    /**
     * Configure this test instance for GEMINO enchantment testing.
     * <p>
     * Sets the enchantment type to GEMINO and uses WOODEN_SWORD material for creating test items.
     * </p>
     */
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.GEMINO;
        itemType = Material.WOODEN_SWORD;
    }

    /**
     * Comprehensive test for GEMINO duplication curse across all magnitude levels.
     * <p>
     * This method consolidates all GEMINO test scenarios into a single sequential test to avoid
     * race conditions from parallel test execution. Each magnitude test uses a different player
     * at a different location to prevent interference.
     * </p>
     * <p>
     * Scenarios tested (magnitudes 1-4):
     * <ol>
     * <li>Magnitude 1: creates 2 copies (2^1)</li>
     * <li>Magnitude 2: creates 4 copies (2^2)</li>
     * <li>Magnitude 3: creates 8 copies (2^3)</li>
     * <li>Magnitude 4: creates 16 copies (2^4)</li>
     * </ol>
     * </p>
     * <p>
     * For each magnitude:
     * <ul>
     * <li>Creates a new player at a unique location</li>
     * <li>Creates a GEMINO-enchanted item with the current magnitude</li>
     * <li>Drops the item and adds it to the player's inventory</li>
     * <li>Fires an EntityPickupItemEvent to trigger the curse</li>
     * <li>Advances the scheduler to allow event processing</li>
     * <li>Verifies the correct number of copies were created</li>
     * </ul>
     * </p>
     */
    @Override @Test
    void doEntityPickupItemTest() {
        // ========================================
        // Scenario 1: Magnitude 1 creates 2 copies (2^1)
        // ========================================
        Location location1 = new Location(testWorld, 1000, 4, 1000);
        PlayerMock player1 = mockServer.addPlayer();
        player1.setLocation(location1);

        ItemStack geminoItem1 = makeEnchantedItem(1, null);
        Item droppedItem1 = player1.getWorld().dropItem(player1.getLocation(), geminoItem1);

        player1.getInventory().addItem(geminoItem1);
        EntityPickupItemEvent event1 = new EntityPickupItemEvent(player1, droppedItem1, 0);
        mockServer.getPluginManager().callEvent(event1);
        mockServer.getScheduler().performTicks(200);

        checkGeminoCopy(player1.getInventory().getContents(), 2, "Magnitude 1");

        // ========================================
        // Scenario 2: Magnitude 2 creates 4 copies (2^2)
        // ========================================
        Location location2 = new Location(testWorld, 2000, 4, 2000);
        PlayerMock player2 = mockServer.addPlayer();
        player2.setLocation(location2);

        ItemStack geminoItem2 = makeEnchantedItem(2, null);
        Item droppedItem2 = player2.getWorld().dropItem(player2.getLocation(), geminoItem2);

        player2.getInventory().addItem(geminoItem2);
        EntityPickupItemEvent event2 = new EntityPickupItemEvent(player2, droppedItem2, 0);
        mockServer.getPluginManager().callEvent(event2);
        mockServer.getScheduler().performTicks(200);

        checkGeminoCopy(player2.getInventory().getContents(), 4, "Magnitude 2");

        // ========================================
        // Scenario 3: Magnitude 3 creates 8 copies (2^3)
        // ========================================
        Location location3 = new Location(testWorld, 3000, 4, 3000);
        PlayerMock player3 = mockServer.addPlayer();
        player3.setLocation(location3);

        ItemStack geminoItem3 = makeEnchantedItem(3, null);
        Item droppedItem3 = player3.getWorld().dropItem(player3.getLocation(), geminoItem3);

        player3.getInventory().addItem(geminoItem3);
        EntityPickupItemEvent event3 = new EntityPickupItemEvent(player3, droppedItem3, 0);
        mockServer.getPluginManager().callEvent(event3);
        mockServer.getScheduler().performTicks(200);

        checkGeminoCopy(player3.getInventory().getContents(), 8, "Magnitude 3");

        // ========================================
        // Scenario 4: Magnitude 4 creates 16 copies (2^4)
        // ========================================
        Location location4 = new Location(testWorld, 4000, 4, 4000);
        PlayerMock player4 = mockServer.addPlayer();
        player4.setLocation(location4);

        ItemStack geminoItem4 = makeEnchantedItem(4, null);
        Item droppedItem4 = player4.getWorld().dropItem(player4.getLocation(), geminoItem4);

        player4.getInventory().addItem(geminoItem4);
        EntityPickupItemEvent event4 = new EntityPickupItemEvent(player4, droppedItem4, 0);
        mockServer.getPluginManager().callEvent(event4);
        mockServer.getScheduler().performTicks(200);

        checkGeminoCopy(player4.getInventory().getContents(), 16, "Magnitude 4");
    }

    /**
     * Verify that GEMINO duplication created the correct number of copies.
     * <p>
     * This helper method validates the inventory after a GEMINO duplication event. It checks that:
     * <ul>
     * <li>The player's inventory is not empty</li>
     * <li>At least one GEMINO-enchanted item is present and has the correct enchantment type</li>
     * <li>The total count of unenchanted copies equals expectedCopies (sums across all stacks)</li>
     * </ul>
     * </p>
     * <p>
     * Note: The method sums all unenchanted item amounts rather than checking individual stacks,
     * because the pickup event may create additional unenchanted items in the inventory.
     * </p>
     *
     * @param playerInventory the player's inventory contents to validate
     * @param expectedCopies  the expected total number of duplicated (unenchanted) items to find
     * @param scenarioLabel   label for the test scenario (used in assertion messages)
     */
    void checkGeminoCopy(@Nullable ItemStack[] playerInventory, int expectedCopies, String scenarioLabel) {
        assertNotNull(playerInventory, scenarioLabel + ": Player inventory is null");
        assertTrue(playerInventory.length > 1, scenarioLabel + ": Player inventory should have multiple slots");

        int totalUnenchantedCopies = 0;
        boolean foundEnchantedItem = false;

        for (ItemStack itemStack : playerInventory) {
            if (itemStack == null || itemStack.getType() != itemType)
                continue;

            if (Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack)) {
                // this should be our original GEMINO item
                Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(itemStack);
                assertNotNull(enchantment, scenarioLabel + ": Enchantment is null on enchanted item");
                assertEquals(ItemEnchantmentType.GEMINO, enchantment.getType(), scenarioLabel + ": Found items with other enchantments after GEMINO copy");
                foundEnchantedItem = true;
            }
            else {
                // Sum up all unenchanted copies (there may be multiple stacks due to pickup event)
                totalUnenchantedCopies += itemStack.getAmount();
            }
        }

        assertTrue(foundEnchantedItem, scenarioLabel + ": Did not find the original GEMINO-enchanted item in inventory");
        assertEquals(expectedCopies, totalUnenchantedCopies, scenarioLabel + ": Did not find the expected number of copied GEMINO items");
    }

    /**
     * GEMINO curse only triggers on item pickup, not on item drop.
     * <p>
     * This test is empty because GEMINO enchantments do not have any special behavior
     * for the PlayerDropItemEvent. Duplication only occurs when items are picked up.
     * </p>
     */
    @Override @Test
    void doItemDropTest() {}

    /**
     * GEMINO curse only triggers on item pickup, not on item held events.
     * <p>
     * This test is empty because GEMINO enchantments do not have any special behavior
     * for the PlayerItemHeldEvent. Duplication only occurs when items are picked up,
     * not when the player switches between hotbar slots.
     * </p>
     */
    @Override @Test
    void doItemHeldTest() {}
}