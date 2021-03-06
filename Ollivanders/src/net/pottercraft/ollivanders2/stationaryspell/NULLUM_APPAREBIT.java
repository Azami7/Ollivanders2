package net.pottercraft.ollivanders2.stationaryspell;

import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Anti-apparition spell.
 *
 * @author lownes
 */
public class NULLUM_APPAREBIT extends StationarySpellObj implements StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.NULLUM_APPAREBIT;
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param pid      the player who cast the spell
    * @param location the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.NULLUM_APPAREBIT;
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
   @NotNull
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData) { }
}