package net.pottercraft.Ollivanders2.Spell;

import java.util.HashSet;
import java.util.Set;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

/**
 * Burns sun-sensitive entities with a radius
 *
 * @author lownes
 * @author Azami7
 */
public final class LUMOS_SOLEM extends Charms
{
   boolean move = true;
   Set<Block> blocks = new HashSet();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_SOLEM ()
   {
      super();

      flavorText.add("Light of the Sun");
      text = "Lumos Solem will cause a sun-like light to erupt in an area around the impact which will burn entities sensitive to sun.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LUMOS_SOLEM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      if (move)
      {
         move();
         if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
         {
            for (LivingEntity live : getLivingEntities(usesModifier))
            {
               boolean burn = false;
               if (live.getType() == EntityType.ZOMBIE)
               {
                  Zombie zombie = (Zombie) live;
                  if (!zombie.isBaby())
                  {
                     burn = true;
                  }
               }
               if (live.getType() == EntityType.SKELETON)
               {
                  burn = true;
               }
               if (burn)
               {
                  live.setFireTicks(160);
               }
            }
            kill = false;
            move = false;
            for (Block block : getBlocksInRadius(location, usesModifier))
            {
               if (block.getType() == Material.AIR)
               {
                  blocks.add(block);
                  block.setType(Material.GLOWSTONE);
               }
            }
         }
      }
      else
      {
         kill();
         for (Block block : blocks)
         {
            block.setType(Material.AIR);
         }
      }
   }
}
