package net.pottercraft.ollivanders2.stationaryspell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collection;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Stationary spell object in Ollivanders2
 *
 * @author lownes
 */
public abstract class StationarySpellObj implements Serializable
{
   Ollivanders2 p;
   final Ollivanders2Common common;
   public UUID playerUUID;
   protected O2StationarySpellType spellType;
   public Location location;
   public int duration;
   public boolean kill = false;
   public boolean active = true;
   public int radius;

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public StationarySpellObj(@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);

      duration = 10;
      radius = 1;
      active = false;
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param playerID the player who cast the spell
    * @param loc      the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public StationarySpellObj(@NotNull Ollivanders2 plugin, @NotNull UUID playerID, @NotNull Location loc, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      p = plugin;
      common = new Ollivanders2Common(p);

      location = loc;
      this.spellType = type;
      playerUUID = playerID;
      this.duration = duration;
      this.radius = radius;
   }

   /**
    * Set the duration of this stationary spell
    *
    * @param d the duration in game ticks
    */
   void setDuration(int d)
   {
      if (d < 0)
         d = 0;

      duration = d;
   }

   /**
    * Set the radius of this stationary spell
    *
    * @param r the radius in blocks
    */
   void setRadius(int r)
   {
      if (r < 0)
         r = 0;

      radius = r;
   }

   /**
    * Set the center location of this stationary spell
    *
    * @param l the spell location
    */
   void setLocation(@NotNull Location l)
   {
      location = l;
   }

   /**
    * Set the ID of the player who cast this spell
    *
    * @param pid the player ID
    */
   void setPlayerID(@NotNull UUID pid)
   {
      playerUUID = pid;
   }

   /**
    * Get the type of this stationary spell
    *
    * @return the spell type
    */
   @NotNull
   public O2StationarySpellType getSpellType()
   {
      return spellType;
   }

   /**
    * Set whether this stationary spell is active
    *
    * @param a true if the spell is active, false otherwise
    */
   void setActive(boolean a)
   {
      active = a;
   }

   /**
    * Ages the StationarySpellObj
    */
   public void age()
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
      duration = duration - i;

      if (duration <= 0)
         kill();
   }

   /**
    * Ages the stationary spell by the specified percent.
    *
    * @param percent the percent to age the spell by
    */
   public void ageByPercent (int percent)
   {
      if (percent < 1)
      {
         percent = 1;
      }
      else if (percent > 100)
      {
         percent = 100;
      }

      age(duration * (percent/100));
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
   public boolean isInside(@NotNull Location loc)
   {
      return Ollivanders2API.common.isInside(location, loc, radius);
   }

   /**
    * Gets the block the projectile is inside
    *
    * @return Block the projectile is inside
    */
   @NotNull
   public Block getBlock ()
   {
      return location.getBlock();
   }

   /**
    * Get living entities who's eye location is within the radius.
    *
    * @return List of living entities with an eye location within radius
    */
   @NotNull
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
      Ollivanders2Common.flair(location, radius, d);
   }

   /**
    * Get the ID of the player that cast the spell
    *
    * @return the MC UUID of the player that cast the spell
    */
   @NotNull
   public UUID getCasterID ()
   {
      return playerUUID;
   }
}