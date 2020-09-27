package net.pottercraft.ollivanders2.stationaryspell;

import org.jetbrains.annotations.NotNull;

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
   @NotNull
   Map<String, String> serializeSpellData ();

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData the serialized spell data
    */
   void deserializeSpellData(@NotNull Map<String, String> spellData);
}