package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 7/2/17.
 *
 * Glacius will cause a great cold to descend in a radius from it's impact point which freezes blocks. The radius and
 * duration of the freeze depend on your experience.
 *
 * @author Azami7
 */
public abstract class GlaciusSuper extends BlockTransfigurationSuper
{
   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GlaciusSuper ()
   {
      super();
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public GlaciusSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      if (usesModifier > 50)
      {
         radius = 5;
      }
      else if (usesModifier < 10)
      {
         radius = 1;
      }
      else
      {
         radius = (int) (usesModifier / 10);
      }
      permanent = false;

      spellDuration = (int)(usesModifier * 1200);

      transfigurationMap.put(Material.FIRE, Material.AIR);
      transfigurationMap.put(Material.WATER, Material.ICE);
      transfigurationMap.put(Material.STATIONARY_WATER, Material.ICE);
      transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
      transfigurationMap.put(Material.STATIONARY_LAVA, Material.OBSIDIAN);
      transfigurationMap.put(Material.ICE, Material.PACKED_ICE);

      materialWhitelist.add(Material.FIRE);
      materialWhitelist.add(Material.WATER);
      materialWhitelist.add(Material.STATIONARY_WATER);
      materialWhitelist.add(Material.LAVA);
      materialWhitelist.add(Material.STATIONARY_LAVA);
      materialWhitelist.add(Material.ICE);
   }

   /*
   @Override
   public void checkEffect ()
   {
      if (move)
      {
         move();
         Block center = getBlock();
         Material type = center.getType();
         double radius = usesModifier * radiusModifier;
         if (type != Material.AIR)
         {
            for (Block block : getBlocksInRadius(location, radius))
            {
               Material changeType = block.getType();
               if (changeType == Material.FIRE)
               {
                  block.setType(Material.AIR);
                  changed.add(block);
               }
               else if (changeType == Material.WATER || changeType == Material.STATIONARY_WATER)
               {
                  block.setType(Material.ICE);
                  changed.add(block);
               }
               else if (changeType == Material.LAVA || changeType == Material.STATIONARY_LAVA)
               {
                  block.setType(Material.OBSIDIAN);
                  changed.add(block);
               }
               else if (changeType == Material.ICE)
               {
                  block.setType(Material.PACKED_ICE);
                  changed.add(block);
               }
            }
            kill = false;
            move = false;
            lifeTicks = (int) (-(usesModifier * 1200 / strengthModifier));
         }
      }
      else
      {
         lifeTicks++;
      }
      if (lifeTicks >= 159)
      {
         revert();
         kill();
      }
   }

   @Override
   public void revert ()
   {
      for (Block block : changed)
      {
         Material mat = block.getType();
         if (mat == Material.PACKED_ICE)
         {
            block.setType(Material.ICE);
         }
         else if (mat == Material.ICE)
         {
            block.setType(Material.STATIONARY_WATER);
         }
         else if (mat == Material.OBSIDIAN)
         {
            block.setType(Material.STATIONARY_LAVA);
         }
         else if (mat == Material.AIR)
         {
            block.setType(Material.FIRE);
         }
      }
   }
   */
}
