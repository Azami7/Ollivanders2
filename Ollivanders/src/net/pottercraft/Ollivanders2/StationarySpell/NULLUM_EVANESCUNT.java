package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Anti-disapparition spell. Players can't apparate out of it.
 *
 * @author lownes
 */
public class NULLUM_EVANESCUNT extends StationarySpellObj implements StationarySpell
{
   public NULLUM_EVANESCUNT (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
   }
}