package net.pottercraft.ollivanders2.stationaryspell;

import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Negates fall damage.
 *
 * @author lownes
 * @author Azami7
 */
public class MOLLIARE extends O2StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public MOLLIARE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.MOLLIARE;
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
   public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.MOLLIARE;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect ()
   {
      age();
   }

   /**
    * Handle player interact event
    *
    * @param event the event
    */
   @Override
   void doOnEntityDamageEvent (@NotNull EntityDamageEvent event)
   {
      if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
         return;

      Entity entity = event.getEntity();

      if (isInside(entity.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("MOLLIARE: canceled EntityDamageEvent", null, null, false);
      }
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData) {}
}