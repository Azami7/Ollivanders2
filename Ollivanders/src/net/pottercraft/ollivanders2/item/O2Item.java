package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Items for Ollivanders
 *
 * @author Azami7
 */
public class O2Item
{
   /**
    * Reference to the plugin
    */
   final protected Ollivanders2 p;

   /**
    * The type this item is
    */
   final private O2ItemType itemType;

   /**
    * Namespace key for NTB flags
    */
   NamespacedKey o2ItemTypeKey;

   /**
    * Constructor
    *
    * @param plugin reference to the plugin
    * @param type   the type this item is
    */
   public O2Item(@NotNull Ollivanders2 plugin, @NotNull O2ItemType type)
   {
      p = plugin;
      itemType = type;

      o2ItemTypeKey = new NamespacedKey(p, "o2enchantment_id");
   }

   /**
    * Get an ItemStack of this item type
    *
    * @param amount the amount of items in the stack
    * @return an ItemStack of this item
    */
   @Nullable
   public ItemStack getItem (int amount)
   {
      Material materialType = itemType.getMaterial();
      short variant = itemType.getVariant();
      String name = itemType.getName();
      ArrayList<String> lore = new ArrayList<>();
      lore.add(itemType.getLore());

      if (Ollivanders2.debug)
      {
         p.getLogger().info("Getting item " + name);
      }

      ItemStack o2Item = new ItemStack(materialType, amount);

      ItemMeta meta = o2Item.getItemMeta();
      if (meta == null)
         return null;

      meta.setLore(lore);
      meta.setDisplayName(name);

      if (materialType == Material.POTION)
      {
         meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
         ((PotionMeta) meta).setColor(O2Color.getBukkitColorByNumber(variant).getBukkitColor());
      }

      PersistentDataContainer container = meta.getPersistentDataContainer();
      container.set(o2ItemTypeKey, PersistentDataType.STRING, name);

      o2Item.setItemMeta(meta);

      return o2Item;
   }

   /**
    * @return the O2ItemType
    */
   @NotNull
   public O2ItemType getType ()
   {
      return itemType;
   }

   /**
    * @return the item name
    */
   @NotNull
   public String getName ()
   {
      return itemType.getName();
   }

   /**
    * @return the material type
    */
   @NotNull
   public Material getMaterialType ()
   {
      return itemType.getMaterial();
   }

   /**
    * Get the item type for this item stack
    *
    * @param itemStack the item stack to check
    * @return the O2ItemType, if it is one, or null otherwise
    */
   @Nullable
   static public O2ItemType getItemType (@NotNull ItemStack itemStack)
   {
      ItemMeta meta = itemStack.getItemMeta();
      if (meta == null)
         return null;

      return O2ItemType.getTypeByName(meta.getDisplayName());
   }
}
