package StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpells;

/**
 * Only players within this can hear other conversation from other
 * players within. Duration depending on spell's level.
 *
 * @author lownes
 */
public class MUFFLIATO extends StationarySpellObj implements StationarySpell
{

   /**
    *
    */
   private static final long serialVersionUID = 4767315283996504871L;

   public MUFFLIATO (Player player, Location location, StationarySpells name,
                     Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
   }

}