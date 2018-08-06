package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.Map;

/**
 * Interface for stationary spells
 *
 * @author lownes
 */
public interface StationarySpell
{
   /**
    * This is the stationary spell's effect. age() must be called in this if you want the spell to age and die eventually.
    *
    * @param p The plugin, so that it can access the list of stationary and projectile spells
    */
   void checkEffect (Ollivanders2 p);

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   Map<String, String> serializeSpellData (Ollivanders2 p);

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   void deserializeSpellData (Map<String, String> spellData, Ollivanders2 p);
}