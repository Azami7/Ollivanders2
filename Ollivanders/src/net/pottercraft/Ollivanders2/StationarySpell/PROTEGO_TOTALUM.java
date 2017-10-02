package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

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
}