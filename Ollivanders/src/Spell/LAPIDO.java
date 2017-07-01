package Spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Created by Aazmi7 on 6/29/17. Imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 */
public class LAPIDO extends Transfiguration implements Spell
{
   public LAPIDO(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect()
   {
      move();
      if (!hasTransfigured())
      {
         if (getBlock().getType() != Material.AIR)
         {
            for (Block block : getBlocksInRadius(location, usesModifier))
            {
               if (block.getType() == Material.COBBLESTONE)
               {
                  block.setType(Material.STONE);
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
            if (location.getBlock().getType() == Material.STONE)
            {
               location.getBlock().setType(Material.COBBLESTONE);
               endTransfigure();
            }
         }
         else
         {
            lifeTicks ++;
            if (location.getBlock().getType() != Material.COBBLESTONE)
            {
               kill = true;
            }
         }
      }
   }
}
