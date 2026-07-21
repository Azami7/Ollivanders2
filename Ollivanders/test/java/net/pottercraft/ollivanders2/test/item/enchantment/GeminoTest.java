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
 * Unit tests for {@link ItemEnchantmentType#GEMINO}.
 *
 * @see ItemEnchantmentType#GEMINO
 */
public class GeminoTest extends EnchantmentTestSuper {
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.GEMINO;
        itemType = Material.WOODEN_SWORD;
    }

    /**
     * Picking up a cursed item adds {@code 2^magnitude} copies to the inventory, verified for magnitudes 1 through 4.
     * All scenarios run in one method with a distinct player per magnitude, since the shared MockBukkit server has
     * mutable state and JUnit does not guarantee method order.
     */
    @Override @Test
    void doEntityPickupItemTest() {
        // magnitude 1 -> 2 copies
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

        // magnitude 2 -> 4 copies
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

        // magnitude 3 -> 8 copies
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

        // magnitude 4 -> 16 copies
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
     * Assert the inventory still holds the original GEMINO-enchanted item and exactly {@code expectedCopies}
     * unenchanted copies, summed across all stacks of {@link #itemType}.
     *
     * @param playerInventory the player's inventory contents to validate
     * @param expectedCopies  the expected total count of duplicated (unenchanted) items
     * @param scenarioLabel   label for the test scenario, used in assertion messages
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
     * GEMINO has no drop behavior; nothing to verify.
     */
    @Override @Test
    void doItemDropTest() {}

    /**
     * GEMINO has no slot-change behavior; nothing to verify.
     */
    @Override @Test
    void doItemHeldTest() {}
}