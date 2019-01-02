package net.pottercraft.Ollivanders2.StationarySpell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collection;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

/**
 * Stationary spell object in Ollivanders2
 *
 * @author lownes
 */
public abstract class StationarySpellObj implements Serializable
{
   Ollivanders2 p;
   public UUID playerUUID;
   protected O2StationarySpellType spellType;
   public Location location;
   public int duration;
   public boolean kill = false;
   public boolean active = true;
   public int radius;

   public StationarySpellObj (Ollivanders2 plugin)
   {
      p = plugin;
      duration = 10;
      radius = 1;
      active = false;
   }

   public StationarySpellObj (Ollivanders2 plugin, UUID playerID, Location loc, O2StationarySpellType type, Integer radius, Integer duration)
   {
      p = plugin;

      location = loc;
      this.spellType = type;
      playerUUID = playerID;
      this.duration = duration;
      this.radius = radius;
   }

   void setDuration (Integer d)
   {
      duration = d;
   }

   void setRadius (Integer r)
   {
      radius = r;
   }

   void setLocation (Location l)
   {
      location = l;
   }

   void setPlayerID (UUID pid)
   {
      playerUUID = pid;
   }

   public O2StationarySpellType getSpellType ()
   {
      return spellType;
   }

   void setSpellType (O2StationarySpellType t)
   {
      spellType = t;
   }

   void setActive (boolean a)
   {
      active = a;
   }

   /**
    * Ages the StationarySpellObj
    */
   public void age ()
   {
      age(1);
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
      if (location == null || loc == null)
         return false;

      return Ollivanders2API.common.isInside(location, loc, radius);
   }

   /**
    * Gets the block the projectile is inside
    *
    * @return Block the projectile is inside
    */
   public Block getBlock ()
   {
      if (location != null)
         return location.getBlock();

      return null;
   }

   /**
    * Get living entities who's eye location is within the radius.
    *
    * @return List of living entities with an eye location within radius
    */
   public List<LivingEntity> getCloseLivingEntities ()
   {
      Collection<LivingEntity> entities = Ollivanders2API.common.getLivingEntitiesInRadius(location, radius);
      List<LivingEntity> close = new ArrayList<>();

      /* only add living entities if their eye location is within the radius */
      for (LivingEntity e : entities)
      {
         if (location.distance(e.getEyeLocation()) < radius)
         {
            close.add(e);
         }
      }

      return close;
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
            Location e = location.clone().add(spherToVec(spher));
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
    * Get the ID of the player that cast the spell
    *
    * @return the MC UUID of the player that cast the spell
    */
   public UUID getCasterID ()
   {
      return playerUUID;
   }
}