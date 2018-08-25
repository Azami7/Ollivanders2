package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Doesn't let entities pass the boundary.
 *
 * @author lownes
 */
public class PROTEGO_TOTALUM extends StationarySpellObj implements StationarySpell
{
   public PROTEGO_TOTALUM (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public PROTEGO_TOTALUM (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                           Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   @Override
   public void checkEffect ()
   {
      age();
      Collection<Entity> nearbyEntities = p.common.getEntitiesInRadius(location, radius + 1);

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
   public void deserializeSpellData (Map<String, String> spellData) { }
}