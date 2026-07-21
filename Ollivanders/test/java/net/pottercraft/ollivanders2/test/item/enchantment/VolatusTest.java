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
 * Unit tests for {@link VOLATUS}.
 */
public class VolatusTest extends EnchantmentTestSuper {
    /**
     * Configure the test to use the VOLATUS enchantment on a stick (the broomstick material).
     */
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.VOLATUS;
        itemType = Material.STICK;
    }

    /**
     * A volatus broom in either hand is detected as held.
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
     * Picking up an item while holding a volatus broom grants BROOM_FLYING once the deferred check runs.
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
     * No volatus-specific inventory pickup behavior to verify.
     */
    @Override @Test
    void doInventoryPickupItemTest() {}

    /**
     * Dropping an item re-evaluates flight: BROOM_FLYING stays while a broom is held and is removed once it is not.
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
     * Switching to a slot holding a volatus broom grants BROOM_FLYING once the deferred check runs.
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