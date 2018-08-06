package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Prevents any block events in an area
 *
 * @author lownes
 */
public class COLLOPORTUS extends StationarySpellObj implements StationarySpell
{
   public COLLOPORTUS (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      // Colloportus duration can only be decreased by an alohomora spell
      if (duration < 1)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Colloportus stationary: kill spell");

         kill();
      }
   }
}