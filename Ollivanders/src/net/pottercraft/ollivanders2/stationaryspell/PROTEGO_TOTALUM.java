package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.UUID;

/**
 * Doesn't let entities pass the boundary.
 *
 * @author lownes
 */
public class PROTEGO_TOTALUM extends ShieldSpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PROTEGO_TOTALUM;
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param pid      the player who cast the spell
    * @param location the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.PROTEGO_TOTALUM;
   }

   @Override
   public void checkEffect ()
   {
      age();
      Collection<Entity> nearbyEntities = Ollivanders2API.common.getEntitiesInRadius(location, radius + 1);

      for (Entity entity : nearbyEntities)
      {
         if (!(entity instanceof Player))
         {
            if (entity.getLocation().distance(location) < radius + 0.5
                  && entity.getLocation().distance(location) > radius - 0.5)
            {
               Location spellLoc = location;
               Location eLoc = entity.getLocation();
               double distance = eLoc.distance(spellLoc);
               if (distance > radius - 0.5)
               {
                  entity.setVelocity(eLoc.toVector()
                        .subtract(spellLoc.toVector()).normalize()
                        .multiply(0.5));
                  flair(10);
               }
               else if (distance < radius + 0.5)
               {
                  entity.setVelocity(spellLoc.toVector()
                        .subtract(eLoc.toVector()).normalize()
                        .multiply(0.5));
                  flair(10);
               }
            }
         }
      }
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   @NotNull
   public Map<String, String> serializeSpellData ()
   {
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData(@NotNull Map<String, String> spellData) { }
}