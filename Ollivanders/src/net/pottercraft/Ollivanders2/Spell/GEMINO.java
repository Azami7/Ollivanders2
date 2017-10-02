package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Places a gemino affect on the item.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class GEMINO extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GEMINO ()
   {
      super();

      flavorText.add("Hermione screamed in pain, and Harry turned his wand on her in time to see a jewelled goblet tumbling from her grip. But as it fell, it split, became a shower of goblets, so that a second later, with a great clatter, the floor was covered in identical cups rolling in every direction, the original impossible to discern amongst them.");
      flavorText.add("The Doubling Curse");
      text = "Gemino will cause an item to duplicate when held by a person.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public GEMINO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         if (p.isWand(item.getItemStack()) || p.isInvisibilityCloak(item.getItemStack()))
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
}