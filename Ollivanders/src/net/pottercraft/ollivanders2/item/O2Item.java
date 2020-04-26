package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;

public class O2Item
{
   protected Ollivanders2 p;

   private O2ItemType itemType;

   public O2Item (Ollivanders2 plugin, O2ItemType type)
   {
      p = plugin;
      itemType = type;
   }

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

      ItemStack ingredient = new ItemStack(materialType, amount, variant);

      ItemMeta meta = ingredient.getItemMeta();
      meta.setLore(lore);
      meta.setDisplayName(name);

      if (materialType == Material.POTION)
      {
         meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
         ((PotionMeta) meta).setColor(O2Color.getBukkitColorByNumber((int) variant).getBukkitColor());
      }

      ingredient.setItemMeta(meta);

      return ingredient;
   }

   public O2ItemType getType ()
   {
      return itemType;
   }

   public String getName ()
   {
      return itemType.getName();
   }

   public Material getMaterialType ()
   {
      return itemType.getMaterial();
   }
}
