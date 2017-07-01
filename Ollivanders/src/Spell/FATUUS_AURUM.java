package Spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Created by Azami7 on 6/29/17.
 *
 * @author Azami7
 */
public class FATUUS_AURUM extends Transfiguration implements Spell
{
   public FATUUS_AURUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect()
   {
      //move();
      if (!hasTransfigured())
      {
         if (getBlock().getType() != Material.AIR)
         {
            for (Block block : getBlocksInRadius(location, usesModifier))
            {
               if (block.getType() == Material.STONE)
               {
                  block.setType(Material.GOLD_BLOCK);
                  kill();
               }
            }
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            kill = true;
            if (location.getBlock().getType() == Material.GOLD_BLOCK)
            {
               location.getBlock().setType(Material.STONE);
               endTransfigure();
            }
         }
         else
         {
            lifeTicks ++;
            if (location.getBlock().getType() != Material.STONE)
            {
               kill = true;
            }
         }
      }
   }
}
