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
   public COLLOPORTUS (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public COLLOPORTUS (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                       Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   public void checkEffect ()
   {
      // Colloportus duration can only be decreased by an alohomora spell
      if (duration < 1)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Colloportus stationary: kill spell");

         kill();
      }
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