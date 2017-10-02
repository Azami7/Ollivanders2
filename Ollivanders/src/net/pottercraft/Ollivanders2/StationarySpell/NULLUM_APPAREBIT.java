package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Anti-apparition spell.
 *
 * @author lownes
 */
public class NULLUM_APPAREBIT extends StationarySpellObj implements StationarySpell
{
   public NULLUM_APPAREBIT (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
   }
}