package Spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Enchants a flying broomstick
 *
 * @author lownes
 */
public class VOLATUS extends SpellProjectile implements Spell
{
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
