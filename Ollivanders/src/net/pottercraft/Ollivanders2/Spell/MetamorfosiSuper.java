package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Basic block transfigurations.
 *
 * @author Azami7
 */
public abstract class MetamorfosiSuper extends Transfiguration
{
   Material targetType = Material.STONE;
   Material transformationType = Material.STONE;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MetamorfosiSuper () { }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public MetamorfosiSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect()
   {
      move();
      if (!hasTransfigured())
      {
         if (getBlock().getType() != Material.AIR)
         {
            for (Block block : getBlocksInRadius(location, usesModifier))
            {
               if (block.getType() == targetType)
               {
                  block.setType(transformationType);
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
            if (location.getBlock().getType() == transformationType)
            {
               location.getBlock().setType(targetType);
               endTransfigure();
            }
         }
         else
         {
            lifeTicks ++;
            if (location.getBlock().getType() != targetType)
            {
               kill = true;
            }
         }
      }
   }
}
