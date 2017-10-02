package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Drops random items from a player's inventory. Also cuts down trees.
 *
 * @author lownes
 * @author Azami7
 */
public final class DIFFINDO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DIFFINDO ()
   {
      super();

      flavorText.add("The Severing Charm");
      flavorText.add("With the Severing Charm, cutting or tearing objects is a simple matter of wand control.");
      flavorText.add("The spell can be quite precise in skilled hands, and the Severing Charm is widely used in a variety of wizarding trades.");
      text = "Breaks logs in a radius or drops items from a player’s inventory.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
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