package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.item.enchantment.VOLATUS;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link VOLATUS} enchantment (broom flight enchantment).
 * <p>
 * Verifies that the VOLATUS enchantment correctly grants the {@link O2EffectType#BROOM_FLYING} effect
 * to players holding volatus-enchanted broomsticks, and properly manages the effect lifecycle through
 * various item interaction events (pickup, drop, held, despawn).
 * </p>
 * <p>
 * Test coverage includes:
 * <ul>
 * <li>Enchantment detection on Ollivanders2 items (O2ItemType.BROOMSTICK)</li>
 * <li>Effect lifecycle on item pickup and drop events</li>
 * <li>Effect maintenance during item slot switching</li>
 * <li>Effect removal when broom is no longer held</li>
 * <li>Item despawn handling (checked directly without scheduler delay)</li>
 * <li>Hopper pickup blocking (no action required)</li>
 * </ul>
 * </p>
 *
 * @see VOLATUS the enchantment implementation being tested
 * @see O2EffectType#BROOM_FLYING the flying effect applied by this enchantment
 */
public class VolatusTest extends EnchantmentTestSuper {
    /**
     * Configure this test instance for VOLATUS enchantment testing.
     * <p>
     * Sets the enchantment type to VOLATUS and uses STICK material for creating test items
     * (matching broomstick material in Minecraft).
     * </p>
     */
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.VOLATUS;
        itemType = Material.STICK;
    }

    /**
     * Test the isHoldingEnchantedItem method across all inventory scenarios.
     * <p>
     * Verifies that the enchantment's isHoldingEnchantedItem() method correctly detects volatus-enchanted items
     * in both main hand and off-hand slots. The method should return true if the player is holding any volatus item
     * in either hand slot.
     * </p>
     * <p>
     * Test cases:
     * <ul>
     * <li>Enchanted broomstick can be retrieved via getEnchantment()</li>
     * <li>isHoldingEnchantedItem() returns true when broomstick in main hand</li>
     * <li>isHoldingEnchantedItem() returns true when broomstick in off-hand</li>
     * </ul>
     * </p>
     */
    @Test
    void isHoldingEnchantedO2ItemTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack broomstick = makeEnchantedItem(1, null);

        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(broomstick);
        assertNotNull(enchantment, "Ollivanders2API.getItems().enchantedItems.getEnchantment(broomstick) returned null");

        // true when holding enchanted item in main hand
        player.getInventory().setItemInMainHand(broomstick);
        assertTrue(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned false when broomstick in main hand");
        player.getInventory().setItemInMainHand(null);

        // true when holding enchanted item in off-hand
        player.getInventory().setItemInOffHand(broomstick);
        assertTrue(enchantment.isHoldingEnchantedItem(player), "enchantment.isHoldingEnchantedItem(player) returned false when broomstick in off hand");
        player.getInventory().setItemInOffHand(null);
    }

    /**
     * Test the doEntityPickupItem event handler.
     * <p>
     * Verifies that when a player picks up an item while holding a volatus-enchanted item, the
     * checkBroomStatus() method is scheduled and subsequently adds the BROOM_FLYING effect.
     * </p>
     * <p>
     * Setup: Player holding volatus-enchanted item picks up another item.
     * Expected: checkBroomStatus() is scheduled via BukkitRunnable with 1-second (20-tick) delay,
     * and after advancing the scheduler, BROOM_FLYING is added to the player.
     * </p>
     */
    @Override @Test
    void doEntityPickupItemTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack broomstick = makeEnchantedItem(1, null);
        Item broomstickItem = player.getWorld().dropItem(player.getLocation(), broomstick);

        // simulate the player picking up the item (put it in inventory)
        player.getInventory().setItemInMainHand(broomstick);

        // fire the pickup event
        EntityPickupItemEvent event = new EntityPickupItemEvent(player, broomstickItem, 1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING),
                "Player should have BROOM_FLYING after entity pickup while holding enchanted broom");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING);
        player.getInventory().setItemInMainHand(null);
    }

    /**
     * Test the doInventoryPickupItem event handler.
     * <p>
     * VOLATUS enchantment has no special handling for inventory (hopper) pickup events.
     * This test is empty as no behavior needs to be verified.
     * </p>
     */
    @Override @Test
    void doInventoryPickupItemTest() {}

    /**
     * Test the doItemDrop event handler.
     * <p>
     * Verifies that when a player drops an item, the broom flying status is re-evaluated. The test covers
     * two scenarios: when the player is holding a volatus-enchanted item (effect should be applied), and
     * when the player is not holding one (effect should be removed).
     * </p>
     * <p>
     * Setup: Player drops items while holding and not holding an enchanted broom.
     * Expected: BROOM_FLYING is applied when holding, removed when not holding. checkBroomStatus() is
     * scheduled with a 1-second (20-tick) delay via BukkitRunnable.
     * </p>
     */
    @Override @Test
    void doItemDropTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack broomstick = makeEnchantedItem(1, null);

        // player holding enchanted broom drops an item
        player.getInventory().setItemInMainHand(broomstick);
        Item droppedItem = player.getWorld().dropItem(player.getLocation(), broomstick);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, droppedItem);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING),
                "Player should have BROOM_FLYING when holding enchanted broom during drop");

        // not holding enchanted item → BROOM_FLYING should be removed
        player.getInventory().setItemInMainHand(null);
        Item droppedItem2 = player.getWorld().dropItem(player.getLocation(), broomstick);

        PlayerDropItemEvent event2 = new PlayerDropItemEvent(player, droppedItem2);
        mockServer.getPluginManager().callEvent(event2);
        mockServer.getScheduler().performTicks(20);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING),
                "BROOM_FLYING should be removed when player is not holding enchanted broom");
    }

    /**
     * Test the doItemHeld event handler.
     * <p>
     * Verifies that when a player changes which item slot is held (switches hotbar slots), the broom flying
     * status is re-evaluated. If the player is holding a volatus-enchanted item after the slot change,
     * the BROOM_FLYING effect should be applied.
     * </p>
     * <p>
     * Setup: Player holds an enchanted broom and triggers a PlayerItemHeldEvent by switching slots.
     * Expected: BROOM_FLYING is applied after the event. checkBroomStatus() is scheduled with a 1-second
     * (20-tick) delay via BukkitRunnable.
     * </p>
     */
    @Override @Test
    void doItemHeldTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack broomstick = makeEnchantedItem(1, null);

        // player holding enchanted broom switches held slot
        player.getInventory().setItemInMainHand(broomstick);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING),
                "Player should have BROOM_FLYING when holding enchanted broom on item held");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING);
        player.getInventory().setItemInMainHand(null);
    }
}