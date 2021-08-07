package net.pottercraft.ollivanders2.item;

import io.netty.resolver.dns.BiDnsQueryLifecycleObserver;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Enchantment effect on items
 *
 * @author Azami7
 */
public class EnchantedItems implements Listener
{
    Ollivanders2 p;

    private Ollivanders2Common common;

    static HashMap<UUID, ItemStack> enchantedItems = new HashMap<>();

    class EnchantedItem
    {
        ItemEnchantmentType enchantmentType;
        boolean permanent = false;
        int duration;
        UUID itemID;

        EnchantedItem (@NotNull UUID id, @NotNull ItemEnchantmentType type, boolean permanent, int duration)
        {
            itemID = id;
            enchantmentType = type;
            this.permanent = permanent;
            this.duration = duration;
        }
    }

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public EnchantedItems (@NotNull Ollivanders2 plugin)
    {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Add enchanted item
     *
     * @param item the the enchanted item
     */
    public void addEnchantedItem (@NotNull Item item)
    {
        enchantedItems.put(item.getUniqueId(), item.getItemStack());

        common.printDebugMessage("Added enchanted item " + item.getName(), null, null, false);
    }

    /**
     * Remove an enchanted item
     *
     * @param itemId the ID of the enchanted item
     */
    public void removeEnchantedItem (@NotNull UUID itemId)
    {
        enchantedItems.remove(itemId);
    }

    /**
     * Remove an enchanted item
     *
     * @param itemStack the itemstack of the enchanted item
     */
    public void removeEnchantedItem (@NotNull ItemStack itemStack)
    {
        if (enchantedItems.values().contains(itemStack))
        {
            Set<UUID> keys = enchantedItems.keySet();
            for (UUID key : keys)
            {
                if (enchantedItems.get(key) == itemStack)
                {
                    enchantedItems.remove(key);
                    return;
                }
            }
        }
    }

    /**
     * Is this an enchanted item or not
     *
     * @param item the item to check
     * @return true if it is an enchanted item, false otherwise
     */
    public boolean isEnchanted (@NotNull Item item)
    {
        UUID itemID = item.getUniqueId();

        return enchantedItems.containsKey(itemID);
    }

    /**
     * Prevent enchanted items from despawning.
     *
     * @param event the item despawn event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void itemDespawnEvent (@NotNull ItemDespawnEvent event)
    {
        Item item = event.getEntity();

        if (!enchantedItems.containsKey(item.getUniqueId()))
            return;

        event.setCancelled(true);
    }

    /**
     * Handle when an enchanted item is picked up.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemPickUp (@NotNull EntityPickupItemEvent event)
    {
        Item item = event.getItem();

        if (!enchantedItems.containsKey(item.getUniqueId()))
            return;


    }

    /**
     * Handle when an enchanted item is dropped.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemDrop (@NotNull PlayerDropItemEvent event)
    {
        Item item = event.getItemDrop();

        if (!enchantedItems.containsKey(item.getUniqueId()))
            return;
    }

    /**
     * Handle when an enchanted item is held.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemHeld (@NotNull PlayerItemHeldEvent event)
    {
    }

    /**
     * Handle when an enchanted item broken.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void itemBreak (@NotNull PlayerItemBreakEvent event)
    {
    }

    /**
     * Handle when an enchanted item damaged.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemDamage (@NotNull PlayerItemDamageEvent event)
    {
    }

    /**
     * Handle when an enchanted item broken.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemMend (@NotNull PlayerItemMendEvent event)
    {
    }

    /**
     * Handle when an enchanted item is moved from one hand to another.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemSwapHands (@NotNull PlayerSwapHandItemsEvent event)
    {
    }
}
