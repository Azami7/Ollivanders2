package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Places a geminio affect on the item.
 *
 * @author lownes
 */
public class GEMINIO extends SpellProjectile implements Spell
{

   public GEMINIO (Ollivanders2 plugin, Player player, Spells name,
                   Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         if (isWand(item.getItemStack()) || isCloak(item.getItemStack()))
         {
            return;
         }
         ItemStack stack = item.getItemStack().clone();
         int amount = stack.getAmount();
         ItemMeta meta = stack.getItemMeta();
         List<String> lore = new ArrayList<String>();
         if (meta.hasLore())
         {
            lore = meta.getLore();
            for (int i = 0; i < lore.size(); i++)
            {
               if (lore.get(i).contains("Geminio "))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  if (magnitude < usesModifier)
                  {
                     magnitude = (int) usesModifier;
                  }
                  lore.set(i, "Geminio " + magnitude);
               }
               else
               {
                  lore.add("Geminio " + (int) usesModifier);
               }
            }
         }
         else
         {
            lore.add("Geminio " + (int) usesModifier);
         }
         meta.setLore(lore);
         stack.setItemMeta(meta);
         stack.setAmount(1);
         if (amount > 1)
         {
            item.getItemStack().setAmount(amount - 1);
         }
         else
         {
            item.remove();
         }
         item.getWorld().dropItem(item.getLocation(), stack);
         kill();
         return;
      }
   }

   /**
    * Is the item a wand?
    *
    * @param held - item to check.
    * @return True if wand, false if not.
    */
   public boolean isWand (ItemStack held)
   {
      if (held.getType() == Material.STICK || held.getType() == Material.BLAZE_ROD)
      {
         if (held.getItemMeta().hasLore())
         {
            List<String> lore = held.getItemMeta().getLore();
            if (lore.get(0).split(" and ").length == 2)
            {
               return true;
            }
            else
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Is the item the cloak of invsibility?
    *
    * @param held - item to check.
    * @return True if cloak, false if not.
    */
   public boolean isCloak (ItemStack held)
   {
      if (held.getType() == Material.CHAINMAIL_CHESTPLATE)
      {
         if (held.getItemMeta().hasLore())
         {
            List<String> lore = held.getItemMeta().getLore();
            if (lore.get(0).equals("Silvery Transparent Cloak"))
            {
               return true;
            }
            else
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }
}