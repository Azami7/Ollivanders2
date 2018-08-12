package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.HashMap;
import java.util.Map;

/**
 * Only players within this can hear other conversation from other players within. Duration depending on spell's level.
 *
 * @author lownes
 */
public class MUFFLIATO extends StationarySpellObj implements StationarySpell
{
   public MUFFLIATO (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public MUFFLIATO (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                     Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

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