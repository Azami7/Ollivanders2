package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Enchants a flying broomstick.
 *
 * @author lownes
 * @author Azami7
 */
public final class VOLATUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VOLATUS ()
   {
      super();

      text = "Volatus is used to enchant a broomstick for flight. "
            + "To make a magical broomstick, you must first craft a broomstick.  This recipe requires two sticks and a wheat."
            + "Place the first stick in the upper-right corner, the next stick in the center, and the wheat in the lower-left. "
            + "Once you have a broomstick, place it in the ground in front of you and cast the spell Volatus at it. "
            + "Your experience with this spell determines how fast the broomstick can go.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public VOLATUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (Item item : getItems(1))
      {
         ItemStack stack = item.getItemStack();
         if (usesModifier >= 1 && isBroom(stack))
         {
            stack.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, (int) usesModifier);
            item.setItemStack(stack);
         }
         return;
      }
   }

   /**
    * Finds out if an item is a broom.
    *
    * @param item - Item in question.
    * @return True if yes.
    */
   public boolean isBroom (ItemStack item)
   {
      if (item.getType() == Material.getMaterial(p.getConfig().getString("broomstick")))
      {
         ItemMeta meta = item.getItemMeta();
         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();
            if (lore.contains("Flying vehicle used by magical folk"))
            {
               return true;
            }
         }
      }
      return false;
   }
}
