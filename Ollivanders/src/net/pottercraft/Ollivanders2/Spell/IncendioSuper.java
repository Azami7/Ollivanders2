package net.pottercraft.Ollivanders2.Spell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 7/2/17.
 *
 * Sets fire to blocks. Also sets fire to living entities and items for an amount of time depending on the player's
 * spell level.
 *
 * @author Azami7
 */
public abstract class IncendioSuper extends Charms
{
   private double lifeTime;
   boolean move;
   boolean strafe = false;
   int radius = 1;
   int blockRadius = 1;
   int distance = 1;
   int duration = 1;
   private int ticksModifier = 16;

   public IncendioSuper () { }

   public IncendioSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lifeTime = usesModifier * ticksModifier;
      move = true;
   }

   public void checkEffect ()
   {
      if (move)
      {
         move();
         //Check if the blocks set on fire are still on fire
         Set<Block> remChange = new HashSet<Block>();
         for (Block block : changed)
         {
            if (block.getType() != Material.FIRE)
            {
               remChange.add(block);
            }
         }
         changed.removeAll(remChange);
         if (strafe)
         {
            for (Block block : getBlocksInRadius(location, blockRadius))
            {
               block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
               if (block.getType() == Material.AIR)
               {
                  block.setType(Material.FIRE);
                  changed.add(block);
               }
            }
         }
         else
         {
            Block block = getBlock();
            Material type = block.getType();
            if (type == Material.AIR)
            {
               block.setType(Material.FIRE);
               changed.add(block);
            }
         }
         List<Item> items = getItems(radius);
         for (Item item : items)
         {
            item.setFireTicks((int)lifeTime);
            if (!strafe)
            {
               kill();
            }
         }
         List<LivingEntity> living = getLivingEntities(radius);
         for (LivingEntity live : living)
         {
            live.setFireTicks((int)lifeTime);
            if (!strafe)
            {
               kill();
            }
         }
         for (SpellProjectile proj : p.getProjectiles())
         {
            if ((proj.name == Spells.GLACIUS || proj.name == Spells.GLACIUS_DUO || proj.name == Spells.GLACIUS_TRIA)
                  && proj.location.getWorld() == location.getWorld())
            {
               if (proj.location.distance(location) < distance)
               {
                  proj.revert();
                  proj.kill();
               }
            }
         }
         if (lifeTicks > lifeTime)
         {
            kill = false;
            move = false;
            lifeTicks = (int) (-(usesModifier * 1200 / duration));
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

   public void revert ()
   {
      for (Block block : changed)
      {
         Material mat = block.getType();
         if (mat == Material.FIRE)
         {
            block.setType(Material.AIR);
         }
      }
   }
}
