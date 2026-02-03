package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import net.pottercraft.ollivanders2.item.enchantment.FLAGRANTE;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link FLAGRANTE} enchantment (burning curse).
 * <p>
 * Verifies that the FLAGRANTE curse correctly applies {@link O2EffectType#FLAGRANTE_BURNING} to players
 * holding flagrante-cursed items, and properly manages the curse lifecycle through various item interaction
 * events (pickup, drop, despawn, inventory pickup).
 * </p>
 * <p>
 * Test coverage includes:
 * <ul>
 * <li>Item despawn cancellation (flagrante items should not despawn from world)</li>
 * <li>Curse application on entity (player) pickup events</li>
 * <li>Hopper/inventory pickup blocking (block inventories cannot pick up cursed items)</li>
 * <li>Curse lifecycle on drop events (added when holding, removed when not holding)</li>
 * <li>Curse status check logic (applied/removed based on current inventory state)</li>
 * <li>No action needed for item held events</li>
 * </ul>
 * </p>
 *
 * @see FLAGRANTE the enchantment implementation being tested
 * @see O2EffectType#FLAGRANTE_BURNING the burning effect applied by this curse
 */
public class FlagranteTest extends EnchantmentTestSuper {
    /**
     * Configure this test instance for FLAGRANTE enchantment testing.
     * <p>
     * Sets the enchantment type to FLAGRANTE and uses WOODEN_SWORD material for creating test items.
     * </p>
     */
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.FLAGRANTE;
        itemType = Material.WOODEN_SWORD;
    }

    /**
     * Test the doEntityPickupItem event handler.
     * <p>
     * Verifies that when a player holding a flagrante-cursed item picks up another item,
     * checkFlagranteStatus() is called and applies FLAGRANTE_BURNING to the player.
     * </p>
     * <p>
     * Setup: Player picks up a flagrante-cursed item while already holding it.
     * Expected: FLAGRANTE_BURNING effect is applied to the player (synchronously, no delay).
     * </p>
     * <p>
     * Note: The EntityPickupItemEvent fires before the item is actually added to the player's inventory.
     * checkFlagranteStatus() checks isHoldingEnchantedItem(), so the item must be in the player's hand
     * before the event fires for the effect to be applied. In game, this occurs when the player picks
     * up an enchanted item while already holding another copy of a flagrante item.
     * </p>
     */
    @Override @Test
    void doEntityPickupItemTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack flagranteItem = makeEnchantedItem(1, null);

        // drop the flagrante item so we can do the pickup event
        Item droppedItem = player.getWorld().dropItem(player.getLocation(), flagranteItem);

        // player must be holding the enchanted item BEFORE the event fires
        // (EntityPickupItemEvent fires before the item is added to inventory)
        player.getInventory().setItemInMainHand(flagranteItem);

        // fire the pickup event for the flagrante item
        EntityPickupItemEvent event = new EntityPickupItemEvent(player, droppedItem, 1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING),
                "Player should have FLAGRANTE_BURNING after picking up flagrante item while holding one");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING);
        player.getInventory().setItemInMainHand(null);
    }

    /**
     * Test the doItemDrop event handler.
     * <p>
     * Verifies that when a player drops an item, the curse status is re-evaluated. The test verifies
     * that dropping an item removes the {@link O2EffectType#FLAGRANTE_BURNING} effect when the player
     * is no longer holding a flagrante-cursed item.
     * </p>
     * <p>
     * Setup: First, an EntityPickupItemEvent is fired while the player holds the enchanted item
     * (to apply the curse), then a PlayerDropItemEvent is fired after the player stops holding it.
     * Expected: FLAGRANTE_BURNING is present after the pickup, and removed after the drop when
     * the player is no longer holding the enchanted item.
     * </p>
     */
    @Override @Test
    void doItemDropTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack enchantedItem = makeEnchantedItem(1, null);

        // first, apply the curse by firing a pickup event while holding the item
        Item droppedItem = player.getWorld().dropItem(player.getLocation(), enchantedItem);
        player.getInventory().setItemInMainHand(enchantedItem);
        EntityPickupItemEvent pickup = new EntityPickupItemEvent(player, droppedItem, 1);
        mockServer.getPluginManager().callEvent(pickup);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING));

        // then, drop the item and verify the curse is removed when no longer holding it
        droppedItem = player.getWorld().dropItem(player.getLocation(), enchantedItem);
        player.getInventory().setItemInMainHand(null);
        PlayerDropItemEvent event = new PlayerDropItemEvent(player, droppedItem);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING),
                "FLAGRANTE_BURNING not removed when player dropped flagrante item");
    }

    /**
     * Test the curse status check logic.
     * <p>
     * Verifies that the flagrante curse is correctly applied and removed based on whether the player
     * is currently holding a flagrante-cursed item. This is a comprehensive lifecycle test that
     * simulates the full curse state transitions.
     * </p>
     * <p>
     * Test cases:
     * <ul>
     * <li>When player holds enchanted item: {@link O2EffectType#FLAGRANTE_BURNING} effect is added</li>
     * <li>When player stops holding enchanted item: {@link O2EffectType#FLAGRANTE_BURNING} effect is removed</li>
     * </ul>
     * </p>
     * <p>
     * This tests the central curse management logic that keeps the player's curse status synchronized
     * with their inventory at all times.
     * </p>
     * <p>
     * Setup: Player holds/doesn't hold enchanted item and triggers drop events.
     * Expected: Curse is added when holding, removed when not holding.
     * </p>
     */
    @Test
    void checkFlagranteStatusTest() {
        PlayerMock player = mockServer.addPlayer();
        ItemStack enchantedItem = new ItemStack(itemType, 1);
        Enchantment enchantment = addEnchantment(enchantedItem, 1, null);

        // holding enchanted item → FLAGRANTE_BURNING should be added
        player.getInventory().setItemInMainHand(enchantedItem);
        Item droppedItem = player.getWorld().dropItem(player.getLocation(), enchantedItem);
        PlayerDropItemEvent dropEvent = new PlayerDropItemEvent(player, droppedItem);
        enchantment.doItemDrop(dropEvent);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING),
                "FLAGRANTE_BURNING should be added when player is holding enchanted item");

        // not holding enchanted item → FLAGRANTE_BURNING should be removed
        player.getInventory().setItemInMainHand(null);
        Item droppedItem2 = player.getWorld().dropItem(player.getLocation(), enchantedItem);
        PlayerDropItemEvent dropEvent2 = new PlayerDropItemEvent(player, droppedItem2);
        enchantment.doItemDrop(dropEvent2);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING),
                "FLAGRANTE_BURNING should be removed when player is not holding enchanted item");
    }

    /**
     * Test the doItemHeld event handler.
     * <p>
     * FLAGRANTE enchantment has no special behavior for player item slot changes. The curse is applied
     * immediately when items are picked up (via the {@link #doEntityPickupItemTest()} and {@link #doItemDropTest()} event handlers),
     * so no additional handling is required when the player changes which item slot is held in their hand
     * (e.g., switching from slot 1 to slot 2 in the hotbar).
     * </p>
     * <p>
     * This test is empty as no behavior needs to be verified.
     * </p>
     */
    @Override @Test
    void doItemHeldTest() {}
}