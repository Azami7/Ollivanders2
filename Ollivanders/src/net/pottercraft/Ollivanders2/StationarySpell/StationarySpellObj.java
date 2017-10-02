package net.pottercraft.Ollivanders2.StationarySpell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.Ollivanders2.OLocation;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Stationary spell object in Ollivanders2
 *
 * @author lownes
 */
public abstract class StationarySpellObj implements Serializable
{

   /**
    *
    */
   private static final long serialVersionUID = 9013964903309999847L;
   public UUID playerUUID;
   public StationarySpells name;
   public OLocation location;
   public int duration;
   public boolean kill;
   public int radius;
   public boolean active;


   public StationarySpellObj (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      this.location = new OLocation(location);
      this.name = name;
      playerUUID = player.getUniqueId();
      kill = false;
      this.duration = duration;
      this.radius = radius;
      active = true;
   }

   /**
    * Ages the StationarySpellObj
    */
   public void age ()
   {
      duration--;
      if (duration < 0)
      {
         kill();
      }
   }

   /**
    * Ages the StationarySpellObj
    *
    * @param i - amount to age
    */
   public void age (int i)
   {
      duration -= i;
      if (duration < 0)
      {
         kill();
      }
   }

   /**
    * This kills the stationarySpellObj.
    */
   public void kill ()
   {
      flair(20);
      kill = true;
   }

   /**
    * Is the location specified inside the object's radius?
    *
    * @param loc - The location specified.
    * @return true if yes, false if no.
    */
   public boolean isInside (Location loc)
   {
      double distance;
      try
      {
         distance = loc.distance(location.toLocation());
      } catch (IllegalArgumentException e)
      {
         return false;
      }
      if (distance < radius)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Gets the block the projectile is inside
    *
    * @return Block the projectile is inside
    */
   public Block getBlock ()
   {
      return location.toLocation().getBlock();
   }

   /**
    * Gets entities within radius of stationary spell
    *
    * @return List of entities within one block of projectile
    */
   public List<Entity> getCloseEntities ()
   {
      List<Entity> entities = new ArrayList<Entity>();
      entities = Bukkit.getServer().getWorld(location.getWorld()).getEntities();
      List<Entity> close = new ArrayList<Entity>();
      for (Entity e : entities)
      {
         if (e instanceof LivingEntity)
         {
            if (location.distance(((LivingEntity) e).getEyeLocation()) < radius)
            {
               close.add(e);
            }
         }
         else
         {
            if (e.getLocation().distance(location.toLocation()) < radius)
            {
               close.add(e);
            }
         }
      }
      return close;
   }

   /**
    * Gets item entities within radius of the projectile
    *
    * @return List of item entities within radius of projectile
    */
   public List<Item> getItems ()
   {
      List<Entity> entities = getCloseEntities();
      List<Item> items = new ArrayList<Item>();
      for (Entity e : entities)
      {
         if (e instanceof Item)
         {
            items.add((Item) e);
         }
      }
      return items;
   }

   /**
    * Gets all LivingEntity within radius of projectile
    *
    * @return List of LivingEntity within radius of projectile
    */
   public List<LivingEntity> getLivingEntities ()
   {
      List<Entity> entities = getCloseEntities();
      List<LivingEntity> living = new ArrayList<LivingEntity>();
      for (Entity e : entities)
      {
         if (e instanceof LivingEntity)
         {
            living.add((LivingEntity) e);
         }
      }
      return living;
   }

   /**
    * Makes a particle effect at all points along the radius of
    * spell and at spell loc
    *
    * @param d - Intensity of the flair. If greater than 10, is reduced to 10.
    */
   public void flair (double d)
   {
      if (d > 10)
      {
         d = 10;
      }
      for (double inc = (Math.random() * Math.PI) / d; inc < Math.PI; inc += Math.PI / d)
      {
         for (double azi = (Math.random() * Math.PI) / d; azi < 2 * Math.PI; azi += Math.PI / d)
         {
            double[] spher = new double[2];
            spher[0] = inc;
            spher[1] = azi;
            Location e = location.toLocation().clone().add(spherToVec(spher));
            e.getWorld().playEffect(e, Effect.SMOKE, 4);
         }
      }
   }

   /**
    * Translates vector to spherical coords
    *
    * @param vec - Vector to be translated
    * @return Spherical coords in double array with
    * indexes 0=inclination 1=azimuth
    */
   @SuppressWarnings("unused")
   private double[] vecToSpher (Vector vec)
   {
      double inc = Math.acos(vec.getZ());
      double azi = Math.atan2(vec.getY(), vec.getX());
      double[] ret = new double[2];
      ret[0] = inc;
      ret[1] = azi;
      return ret;
   }

   /**
    * Translates spherical coords to vector
    *
    * @param spher array with indexes 0=inclination 1=azimuth
    * @return Vector
    */
   private Vector spherToVec (double[] spher)
   {
      double inc = spher[0];
      double azi = spher[1];
      double x = radius * Math.sin(inc) * Math.cos(azi);
      double z = radius * Math.sin(inc) * Math.sin(azi);
      double y = radius * Math.cos(inc);
      Vector ret = new Vector(x, y, z);
      return ret;
   }

   /**
    * Does the player hold a wand item?
    *
    * @param player - Player to check.
    * @return True if the player holds a wand. False if not.
    */
   public boolean holdsWand (Player player)
   {
      //if (player.getItemInHand() != null){
      if (player.getInventory().getItemInMainHand() != null)
      {
         //ItemStack held = player.getItemInHand();
         ItemStack held = player.getInventory().getItemInMainHand();
         if (held.getType() == Material.STICK || held.getType() == Material.BLAZE_ROD)
         {
            if (held.getItemMeta().hasLore())
            {
               List<String> lore = held.getItemMeta().getLore();
               if (lore.get(0).split(" and ").length == 2)
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            else
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Gets the blocks in a radius of a location.
    *
    * @param loc    - The Location that is the center of the block list
    * @param radius - The radius of the block list
    * @return List of blocks that are within radius of the location.
    */
   public Set<Block> getBlocksInRadius (Location loc, double radius)
   {
      Block center = loc.getBlock();
      int blockRadius = (int) (radius + 1);
      Set<Block> blockList = new HashSet<Block>();
      for (int x = -blockRadius; x <= blockRadius; x++)
      {
         for (int y = -blockRadius; y <= blockRadius; y++)
         {
            for (int z = -blockRadius; z <= blockRadius; z++)
            {
               blockList.add(center.getRelative(x, y, z));
            }
         }
      }
      Set<Block> returnList = new HashSet<Block>();
      for (Block block : blockList)
      {
         if (block.getLocation().distance(center.getLocation()) < radius)
         {
            returnList.add(block);
         }
      }
      return returnList;
   }

   public UUID getPlayerUUID ()
   {
      return playerUUID;
   }

}