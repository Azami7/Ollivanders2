package StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpells;

/**
 * Anti-apparition spell.
 *
 * @author lownes
 */
public class NULLUM_APPAREBIT extends StationarySpellObj implements StationarySpell
{

   /**
    *
    */
   private static final long serialVersionUID = 8858656671784513666L;

   public NULLUM_APPAREBIT (Player player, Location location,
                            StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
   }

}