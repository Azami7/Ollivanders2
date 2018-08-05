package net.pottercraft.Ollivanders2.StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.HashMap;
import java.util.Map;

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

   public NULLUM_APPAREBIT (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                            Map<String, String> spellData, Ollivanders2 plugin)
   {
      super(player, location, name, radius, duration);

      deserializeSpellData(spellData, plugin);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
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