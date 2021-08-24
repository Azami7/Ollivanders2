package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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

    static HashMap<UUID, EnchantedItem> enchantedItems = new HashMap<>();

    private class EnchantedItem
    {
        ItemStack item;
        Enchantment enchantment;

        EnchantedItem (@NotNull ItemStack i, @NotNull Enchantment e)
        {
            item = i;
            enchantment = e;
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
     * @param item the enchanted item
     * @param enchantmentType the type of enchantment
     * @param magnitude the magnitude of enchantment
     */
    public void addEnchantedItem (@NotNull Item item, @NotNull ItemEnchantmentType enchantmentType, int magnitude)
    {
        Enchantment enchantment = null;
        Class<?> enchantmentClass = enchantmentType.getClassName();

        try
        {
            enchantment = (Enchantment)enchantmentClass.getConstructor(Ollivanders2.class, int.class).newInstance(p, magnitude);
        }
        catch (Exception e)
        {
            common.printDebugMessage("Failed to get enchantment class", e, null, true);
        }

        if (enchantment == null)
            return;

        if (!enchantedItems.containsKey(item.getUniqueId()))
        {
            enchantedItems.put(item.getUniqueId(), new EnchantedItem(item.getItemStack(), enchantment));
            common.printDebugMessage("Added enchanted item " + item.getName() + " of type " + enchantment.enchantmentType.getName(), null, null, false);
        }
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
                if (enchantedItems.get(key).item == itemStack)
                {
                    enchantedItems.remove(key);
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
     * Get the enchantment for an item.
     *
     * @param itemID the ID of the enchanted item
     * @return the enchantment on this item
     */
    @Nullable
    public Enchantment getEnchantment (UUID itemID)
    {
        if (enchantedItems.containsKey(itemID))
        {
            return enchantedItems.get(itemID).enchantment;
        }

        return null;
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

        Entity entity = event.getEntity();
        if (entity instanceof Player)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    enchantedItems.get(item.getUniqueId()).enchantment.doItemPickup(item.getItemStack(), (Player) entity);
                }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 5);
        }
    }

    /**
     * Handle when an enchanted item is dropped.
     *
     * @param event
     */
    /*
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemDrop (@NotNull PlayerDropItemEvent event)
    {
        Item item = event.getItemDrop();

        if (!enchantedItems.containsKey(item.getUniqueId()))
            return;
    }

     */

    /**
     * Handle when an enchanted item is held.
     *
     * @param event
     */
    /*
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemHeld (@NotNull PlayerItemHeldEvent event)
    {
    }

     */

    /**
     * Handle when an enchanted item broken.
     *
     * @param event
     */
    /*
    @EventHandler(priority = EventPriority.LOWEST)
    public void itemBreak (@NotNull PlayerItemBreakEvent event)
    {
    }

     */

    /**
     * Handle when an enchanted item damaged.
     *
     * @param event
     */
    /*
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemDamage (@NotNull PlayerItemDamageEvent event)
    {
    }

     */

    /**
     * Handle when an enchanted item broken.
     *
     * @param event
     */
    /*
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemMend (@NotNull PlayerItemMendEvent event)
    {
    }

     */

    /**
     * Handle when an enchanted item is moved from one hand to another.
     *
     * @param event
     */
    /*
    @EventHandler(priority = EventPriority.NORMAL)
    public void itemSwapHands (@NotNull PlayerSwapHandItemsEvent event)
    {
    }

     */
}
