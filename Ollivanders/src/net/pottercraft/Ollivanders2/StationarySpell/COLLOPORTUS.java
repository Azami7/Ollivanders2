package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.Map;
import java.util.HashMap;

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

   public COLLOPORTUS (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                       Map<String, String> spellData, Ollivanders2 plugin)
   {
      super(player, location, name, radius, duration);

      deserializeSpellData(spellData, plugin);
   }

   public void checkEffect (Ollivanders2 p)
   {
      if (duration < 1)
         kill();
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