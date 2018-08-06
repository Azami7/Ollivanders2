package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.HashMap;
import java.util.Map;

/**
 * Doesn't let entities pass the boundary.
 *
 * @author lownes
 */
public class PROTEGO_TOTALUM extends StationarySpellObj implements StationarySpell
{
   public PROTEGO_TOTALUM (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public PROTEGO_TOTALUM (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                           Map<String, String> spellData, Ollivanders2 plugin)
   {
      super(player, location, name, radius, duration);

      deserializeSpellData(spellData, plugin);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
      for (Entity entity : Bukkit.getServer().getWorld(location.getWorld()).getEntities())
      {
         if (!(entity instanceof Player))
         {
            if (entity.getLocation().distance(location.toLocation()) < radius + 0.5
                  && entity.getLocation().distance(location.toLocation()) > radius - 0.5)
            {
               Location spellLoc = location.toLocation();
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
    * @param p unused for this spell
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData (Ollivanders2 p)
   {
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    * @param p unused for this spell
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData, Ollivanders2 p) { }
}