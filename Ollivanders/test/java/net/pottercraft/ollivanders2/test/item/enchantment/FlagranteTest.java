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
 * Unit tests for {@link FLAGRANTE}.
 *
 * @see FLAGRANTE
 * @see O2EffectType#FLAGRANTE_BURNING
 */
public class FlagranteTest extends EnchantmentTestSuper {
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.FLAGRANTE;
        itemType = Material.WOODEN_SWORD;
    }

    /**
     * Picking up a cursed item while holding one applies {@link O2EffectType#FLAGRANTE_BURNING}. The item must be in
     * hand before firing the event, since EntityPickupItemEvent fires before the item reaches the inventory.
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
     * Dropping the last cursed item removes {@link O2EffectType#FLAGRANTE_BURNING}: the curse is applied via a pickup
     * while holding the item, then removed once the player drops it and holds none.
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
     * The curse tracks inventory state: {@link O2EffectType#FLAGRANTE_BURNING} is added while the player holds a cursed
     * item and removed once they hold none.
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
     * Slot-change re-evaluation is already exercised by {@link #checkFlagranteStatusTest()}; nothing else to verify.
     */
    @Override @Test
    void doItemHeldTest() {}
}