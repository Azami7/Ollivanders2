package Spell;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Drops random items from a player's inventory. Also cuts down trees.
 *
 * @author lownes
 */
public class DIFFINDO extends SpellProjectile implements Spell
{
   public DIFFINDO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Player)
         {
            PlayerInventory inv = ((Player) live).getInventory();
            ArrayList<ItemStack> remStack = new ArrayList<ItemStack>();
            for (ItemStack stack : inv.getContents())
            {
               if (stack != null)
               {
                  if (Math.random() * usesModifier > 1)
                  {
                     remStack.add(stack);
                  }
               }
            }
            for (ItemStack rem : remStack)
            {
               inv.remove(rem);
               live.getWorld().dropItemNaturally(live.getLocation(), rem);
            }
            kill();
            return;
         }
      }
      if (getBlock().getType() == Material.LOG)
      {
         for (Block block : getBlocksInRadius(location, usesModifier))
         {
            if (block.getType() == Material.LOG)
            {
               block.breakNaturally();
            }
         }
         kill();
      }
   }
}