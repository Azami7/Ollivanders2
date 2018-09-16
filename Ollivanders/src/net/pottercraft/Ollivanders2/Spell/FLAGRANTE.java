package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Places a flagrante affect on the item.
 *
 * @author lownes
 */
public final class FLAGRANTE extends DarkArts
{
   public O2SpellType spellType = O2SpellType.FLAGRANTE;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("Burning Curse");
      add("They have added Geminio and Flagrante curses! Everything you touch will burn and multiply, but the copies are worthless.");
   }};

   protected String text = "Flagrante will cause an item to burn it's bearer when picked up.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FLAGRANTE () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FLAGRANTE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         if (p.common.isWand(item.getItemStack()))
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
               if (lore.get(i).contains("Flagrante "))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  if (magnitude < usesModifier)
                  {
                     magnitude = (int) usesModifier;
                  }
                  lore.set(i, "Flagrante " + magnitude);
               }
               else
               {
                  lore.add("Flagrante " + (int) usesModifier);
               }
            }
         }
         else
         {
            lore.add("Flagrante " + (int) usesModifier);
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
}