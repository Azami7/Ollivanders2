package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Anti-disapparition spell. Players can't apparate out of it.
 *
 * @author lownes
 */
public class NULLUM_EVANESCUNT extends StationarySpellObj implements StationarySpell
{
   public NULLUM_EVANESCUNT (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public NULLUM_EVANESCUNT (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                             Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   @Override
   public void checkEffect ()
   {
      age();
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