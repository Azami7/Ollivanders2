package net.pottercraft.Ollivanders2.StationarySpell;

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
    */
   void checkEffect ();

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   Map<String, String> serializeSpellData ();

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData the serialized spell data
    */
   void deserializeSpellData (Map<String, String> spellData);
}