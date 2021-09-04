package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Enchantment effect on items
 *
 * @author Azami7
 */
public class EnchantedItems implements Listener
{
    /**
     * Callback to the plugin
     */
    Ollivanders2 p;

    /**
     * Common functions
     */
    private final Ollivanders2Common common;

    /**
     * Namespace keys for NBT tags
     */
    NamespacedKey enchantmentType;
    NamespacedKey enchantmentMagnitude;
    NamespacedKey enchantmentID;

    /**
     * Lookup of known enchanted items to speed up handlers
     */
    HashMap<String, Enchantment> enchantedItems = new HashMap<>();

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public EnchantedItems (@NotNull Ollivanders2 plugin)
    {
        p = plugin;
        common = new Ollivanders2Common(p);

        enchantmentType = new NamespacedKey(p, "o2enchantment_name");
        enchantmentMagnitude = new NamespacedKey(p, "o2enchantment_magnitude");
        enchantmentID = new NamespacedKey(p, "o2enchantment_id");
    }

    /**
     * Add enchanted item
     *
     * @param item the enchanted item
     * @param eType the type of enchantment
     * @param magnitude the magnitude of enchantment
     */
    public void addEnchantedItem (@NotNull Item item, @NotNull ItemEnchantmentType eType, int magnitude)
    {
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta == null)
            return;

        String eid = item.getUniqueId().toString();

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(enchantmentID, PersistentDataType.STRING, eid);
        container.set(enchantmentMagnitude, PersistentDataType.INTEGER, magnitude);
        container.set(enchantmentType, PersistentDataType.STRING, eType.toString());

        item.getItemStack().setItemMeta(itemMeta);

        Enchantment enchantment = getEnchantment(item.getItemStack());
        if (enchantment == null)
            return;

        if (enchantment.lore != null)
        {
            List<String> lore = itemMeta.getLore();
            if (lore == null)
            {
                lore = new ArrayList<>();
            }
            lore.add(enchantment.lore);
            itemMeta.setLore(lore);
            item.getItemStack().setItemMeta(itemMeta);
        }

        // store these in a hashmap for faster access later
        enchantedItems.put(eid, enchantment);

        common.printDebugMessage("Added enchanted item " + item.getName() + " of type " + eType.getName(), null, null, false);
    }

    /**
     * Determine if an item stack is enchanted.
     *
     * @param item the item to check
     * @return true if the item stack has the NBT tag, false otherwise
     */
    public boolean isEnchanted (@NotNull Item item)
    {
        return isEnchanted(item.getItemStack());
    }

    /**
     * Determine if an item stack is enchanted.
     *
     * @param itemStack the item to check
     * @return true if the item stack has the NBT tag, false otherwise
     */
    public boolean isEnchanted (@NotNull ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentType, PersistentDataType.STRING);
    }

    /**
     * Get the enchantment key for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the key if present, null otherwise
     */
    @Nullable
    public String getEnchantmentType (@NotNull ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if(container.has(enchantmentType, PersistentDataType.STRING))
        {
            return container.get(enchantmentType, PersistentDataType.STRING);
        }
        else
            return null;
    }

    /**
     * Get the enchantment id for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the id if present, null otherwise
     */
    @Nullable
    public String getEnchantmentID (@NotNull ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if(container.has(enchantmentID, PersistentDataType.STRING))
        {
            return container.get(enchantmentID, PersistentDataType.STRING);
        }
        else
            return null;
    }

    /**
     * Get the enchantment magnitude for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the id if present, -1 otherwise
     */
    public Integer getEnchantmentMagnitude (@NotNull ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return -1;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if(container.has(enchantmentMagnitude, PersistentDataType.INTEGER))
        {
            return container.get(enchantmentMagnitude, PersistentDataType.INTEGER);
        }
        else
            return -1;
    }

    /**
     * Get the enchantment object for this enchanted item - this will either get the existing or create one
     *
     * @param itemStack the enchanted item
     * @return the Enchantment if enchantment NBT tags are present, null otherwise
     */
    @Nullable
    public Enchantment getEnchantment (@NotNull ItemStack itemStack)
    {
        if (!isEnchanted(itemStack))
            return null;

        String eid = getEnchantmentID(itemStack);

        if (eid == null)
            return null;

        // have we already loaded this one?
        if (enchantedItems.containsKey(eid))
        {
            return enchantedItems.get(eid);
        }

        Integer magnitude = getEnchantmentMagnitude(itemStack);
        String eType = getEnchantmentType(itemStack);

        if (magnitude == null || eType == null)
        {
            common.printDebugMessage("Null enchantment NBT tags", null, null, false);
            return null;
        }

        return createEnchantment(eType, magnitude);
    }

    /**
     * Create the enchantment object for an enchanted item
     *
     * @param eType the type of enchantment
     * @param magnitude the magnitude of the enchantment
     * @return the enchantment if type and magnitude were valid, null otherwise
     */
    @Nullable
    public Enchantment createEnchantment (@NotNull String eType, @NotNull Integer magnitude)
    {
        if (magnitude < 1)
            return null;

        ItemEnchantmentType enchantmentType;

        try
        {
            enchantmentType = ItemEnchantmentType.valueOf(eType);
        }
        catch (Exception e)
        {
            common.printDebugMessage("Malformed enchantment type", null, null, false);
            return null;
        }

        Enchantment enchantment = null;
        Class<?> enchantmentClass = enchantmentType.getClassName();

        try
        {
            enchantment = (Enchantment)enchantmentClass.getConstructor(Ollivanders2.class, int.class, String.class).newInstance(p, magnitude, enchantmentType.getLore());
        }
        catch (Exception e)
        {
            common.printDebugMessage("Failed to create enchantment", e, null, true);
        }

        return enchantment;
    }

    /**
     * Handle enchanted items despawning.
     *
     * @param event the item despawn event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawn (@NotNull ItemDespawnEvent event)
    {
        Item item = event.getEntity();

        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        p.getLogger().info("item despawn event");
        enchantment.doItemDespawn(event);
    }

    /**
     * Handle when an enchanted item is picked up.
     *
     * @param event the item pick up event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickup (@NotNull EntityPickupItemEvent event)
    {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        p.getLogger().info("item pickup event");
        enchantment.doItemPickup(event);
    }

    /**
     * Handle when an enchanted item is dropped.
     *
     * @param event the item drop event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDrop (@NotNull PlayerDropItemEvent event)
    {
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        p.getLogger().info("item drop event");
        enchantment.doItemDrop(event);
    }

    /**
     * Handle when an enchanted item is held.
     *
     * @param event the player item held event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeld (@NotNull PlayerItemHeldEvent event)
    {
        p.getLogger().info("player item held event");

        Player player = event.getPlayer();

        // check newly held item
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());

        if (heldItem != null && isEnchanted(heldItem))
        {
            p.getLogger().info("held enchanted item");
        }
        else
        {
            // check previously held item
            heldItem = player.getInventory().getItem(event.getPreviousSlot());
            if (heldItem != null && isEnchanted(heldItem))
            {
                if (isEnchanted(heldItem))
                   p.getLogger().info("stopped holding enchanted item");
            }
            else
            {
                p.getLogger().info("neither held nor previous item is enchanted");
                return;
            }
        }

        Enchantment enchantment = getEnchantment(heldItem);
        if (enchantment == null)
            return;

        p.getLogger().info("item held event");
        enchantment.doItemHeld(event);
    }

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
    @EventHandler(priority = EventPriority.LOWEST)
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
    @EventHandler(priority = EventPriority.LOWEST)
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
    @EventHandler(priority = EventPriority.LOWEST)
    public void itemSwapHands (@NotNull PlayerSwapHandItemsEvent event)
    {
    }

     */
}
