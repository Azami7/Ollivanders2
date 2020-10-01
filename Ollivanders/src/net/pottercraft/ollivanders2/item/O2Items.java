package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class O2Items
{
   private Ollivanders2 p;

   private HashMap<O2ItemType, O2Item> O2ItemMap = new HashMap<>();

   public O2Items (Ollivanders2 plugin)
   {
      p = plugin;

      initItems();
   }

   private void initItems ()
   {
      for (O2ItemType itemType : O2ItemType.values())
      {
         O2Item item = new O2Item(p, itemType);
         O2ItemMap.put(item.getType(), item);
      }
   }

   /**
    * Get an item by type
    *
    * @param itemType the type of item to get
    * @param amount   the amount of the item to get
    * @return an item stack of the item and amount, null if item not found
    */
   public ItemStack getItemByType (O2ItemType itemType, int amount)
   {
      if (itemType == null || amount < 1)
      {
         return null;
      }

      O2Item item = O2ItemMap.get(itemType);

      return item.getItem(amount);
   }

   /**
    * Get an item by name
    *
    * @param name   the name of the item
    * @param amount the amount of the item to get
    * @return an item stack of the item and amount, null if item not found
    */
   public ItemStack getItemByName (String name, int amount)
   {
      O2ItemType itemType = getItemTypeByName(name);

      return getItemByType(itemType, amount);
   }

   /**
    * Get an item by display name - set in the metadata of the item
    *
    * @param name   the display name for the item
    * @param amount the amount of the item to get
    * @return an item stack of the item and amount, null if item not found
    */
   public ItemStack getItemByDisplayName (String name, int amount)
   {
      O2ItemType itemType = getTypeByDisplayName(name);

      return getItemByType(itemType, amount);
   }

   /**
    * Get item type by display name - set in the metadata of the item
    *
    * @param name the display name for the item
    * @return the item type if found, null otherwise
    */
   public O2ItemType getTypeByDisplayName (String name)
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
   public ItemStack getItemStartsWith (String name, int amount)
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
   public String getItemDisplayNameByType (O2ItemType itemType)
   {
      if (itemType == null)
      {
         return null;
      }

      return O2ItemMap.get(itemType).getName();
   }

   /**
    * Get the type of an item from the name. This must be a full match on the string.
    *
    * @param name the name of the item type as a string
    * @return the item type if found, null otherwise
    */
   public O2ItemType getItemTypeByName (String name)
   {
      String itemTypeString = name.trim().toUpperCase().replaceAll(" ", "_");
      O2ItemType itemType = null;

      try
      {
         itemType = O2ItemType.valueOf(itemTypeString);
      }
      catch (Exception e)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("No item type " + itemTypeString + " found.");
         }
      }

      return itemType;
   }

   /**
    * Get an item where the name starts with this string
    *
    * @param name the substring of the name starts with
    * @return the item type if found, null otherwise
    */
   public O2ItemType getItemTypeByStartsWith (String name)
   {
      O2ItemType itemType = null;

      for (O2Item item : O2ItemMap.values())
      {
         if (item.getName().toLowerCase().startsWith(name.trim().toLowerCase()))
         {
            return item.getType();
         }
      }

      return null;
   }

   /**
    * Get the material type for an item type
    *
    * @param itemType the item type to get the material for
    * @return the material type if found, null otherwise
    */
   public Material getItemMaterialByType(O2ItemType itemType)
   {
      if (itemType == null)
      {
         return null;
      }

      return O2ItemMap.get(itemType).getMaterialType();
   }

   /**
    * Get the names of all items.
    *
    * @return an array of the item names
    */
   public ArrayList<String> getAllItems()
   {
      Set<O2ItemType> itemKeys = O2ItemMap.keySet();

      ArrayList<String> itemNames = new ArrayList<>();

      for (O2ItemType itemType : itemKeys)
      {
         itemNames.add(itemType.getName());
      }

      return itemNames;
   }
}
