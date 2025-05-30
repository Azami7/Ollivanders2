package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.item.enchantment.EnchantedItems;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.item.wand.O2Wands;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Manager for all items in Ollivanders
 */
public class O2Items
{
    /**
     * A reference to the plugin
     */
    final private Ollivanders2 p;

    /**
     * A map of all items in the game by type
     */
    final private HashMap<O2ItemType, O2Item> O2ItemMap = new HashMap<>();

    /**
     * The enchanted items manager - handles all enchantments on items
     */
    public EnchantedItems enchantedItems;

    /**
     * Common functions
     */
    private Ollivanders2Common common;

    /**
     * The wand manager - wands being complex items with special logic
     */
    static O2Wands wands;

    /**
     * Namespace key for NTB flags
     */
    public static NamespacedKey o2ItemTypeKey;

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin
     */
    public O2Items(@NotNull Ollivanders2 plugin)
    {
        p = plugin;
        common = new Ollivanders2Common(p);
        wands = new O2Wands(p);

        enchantedItems = new EnchantedItems(p);
        p.getServer().getPluginManager().registerEvents(enchantedItems, p);

        o2ItemTypeKey = new NamespacedKey(p, "o2enchantment_id");
    }

    /**
     * Load all items and enchantments
     */
    public void onEnable()
    {
        for (O2ItemType itemType : O2ItemType.values())
        {
            O2Item item = new O2Item(p, itemType);
            O2ItemMap.put(item.getType(), item);
        }

        enchantedItems.onEnable();
    }

    /**
     * Get an item by type
     *
     * @param itemType the type of item to get
     * @param amount   the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemByType(@NotNull O2ItemType itemType, int amount)
    {
        if (amount < 1)
        {
            return null;
        }

        O2Item o2item = O2ItemMap.get(itemType);
        ItemStack itemStack = o2item.getItem(amount);

        // does this item need an enchantment on it?
        ItemEnchantmentType enchantment = itemType.getItemEnchantment();
        if (itemStack != null && enchantment != null)
        {
            String eid = TimeCommon.getCurrentTimestamp() + " " + itemType.getName() + " " + enchantment.getName();
            enchantedItems.addEnchantedItem(itemStack, enchantment, 1, eid, "");
        }

        return itemStack;
    }

    /**
     * Get an item by name
     *
     * @param name   the name of the item
     * @param amount the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemByName(@NotNull String name, int amount)
    {
        O2ItemType itemType = getItemTypeByName(name);
        if (itemType == null)
            return null;

        return getItemByType(itemType, amount);
    }

    /**
     * Get an item by display name - set in the metadata of the item
     *
     * @param name   the display name for the item
     * @param amount the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemByDisplayName(@NotNull String name, int amount)
    {
        O2ItemType itemType = getTypeByDisplayName(name);
        if (itemType == null)
            return null;

        return getItemByType(itemType, amount);
    }

    /**
     * Get item type by display name - set in the metadata of the item
     *
     * @param name the display name for the item
     * @return the item type if found, null otherwise
     */
    @Nullable
    public O2ItemType getTypeByDisplayName(@NotNull String name)
    {
        O2ItemType itemType = null;

        for (O2Item item : O2ItemMap.values())
        {
            if (item.getName().toLowerCase().equals(name.toLowerCase()))
            {
                itemType = item.getType();
                break;
            }
        }

        return itemType;
    }

    /**
     * Get an item that name starts with a substring
     *
     * @param name   the substring of the name starts with
     * @param amount the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemStartsWith(@NotNull String name, int amount)
    {
        O2ItemType itemType = getItemTypeByStartsWith(name);

        if (itemType == null)
            return null;

        return getItemByType(itemType, amount);
    }

    /**
     * Get the name of an item by type.
     *
     * @param itemType the item type
     * @return the name of this item or null if not found
     */
    @Nullable
    public String getItemDisplayNameByType(@NotNull O2ItemType itemType)
    {
        return O2ItemMap.get(itemType).getName();
    }

    /**
     * Get the type of an item from the name. This must be a full match on the string.
     *
     * @param name the name of the item type as a string
     * @return the item type if found, null otherwise
     */
    @Nullable
    public O2ItemType getItemTypeByName(@NotNull String name)
    {
        String itemTypeString = name.trim().toUpperCase().replaceAll(" ", "_");
        O2ItemType itemType = null;

        try
        {
            itemType = O2ItemType.valueOf(itemTypeString);
        }
        catch (Exception e)
        {
            common.printDebugMessage("No item type " + itemTypeString + " found.", null, null, false);
        }

        return itemType;
    }

    /**
     * Get an item where the name starts with this string
     *
     * @param name the substring of the name starts with
     * @return the item type if found, null otherwise
     */
    @Nullable
    public O2ItemType getItemTypeByStartsWith(@NotNull String name)
    {
        for (O2Item item : O2ItemMap.values())
        {
            if (item.getName().toLowerCase().startsWith(name.trim().toLowerCase()))
                return item.getType();
        }

        return null;
    }

    /**
     * Get the material type for an item type
     *
     * @param itemType the item type to get the material for
     * @return the material type if found, null otherwise
     */
    @Nullable
    public Material getItemMaterialByType(@NotNull O2ItemType itemType)
    {
        return O2ItemMap.get(itemType).getMaterialType();
    }

    /**
     * Get the names of all items.
     *
     * @return an array of the item names
     */
    @NotNull
    public ArrayList<String> getAllItems()
    {
        Set<O2ItemType> itemKeys = O2ItemMap.keySet();

        ArrayList<String> itemNames = new ArrayList<>();

        for (O2ItemType itemType : itemKeys)
        {
            itemNames.add(itemType.getName());
        }

        Collections.sort(itemNames);
        return itemNames;
    }

    /**
     * Get wands management class
     *
     * @return wands
     */
    public O2Wands getWands()
    {
        return wands;
    }
}
