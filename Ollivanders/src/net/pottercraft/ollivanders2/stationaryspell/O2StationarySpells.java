package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class O2StationarySpells
{
   private List<O2StationarySpell> O2StationarySpells = new ArrayList<>();
   Ollivanders2 p;
   Ollivanders2Common common;

   private final String playerUUIDLabel = "Player_UUID";
   private final String spellLabel = "Name";
   private final String durationLabel = "Duration";
   private final String radiusLabel = "Radius";
   private final String spellLocLabel = "Spell_Loc";

   /**
    * Constructor
    *
    * @param plugin a reference to the plugin
    */
   public O2StationarySpells(@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(plugin);

      loadO2StationarySpells();
   }

   /**
    * Add a stationary spell
    *
    * @param spell the stationary spell to add
    */
   public void addStationarySpell(@NotNull O2StationarySpell spell)
   {
      common.printDebugMessage("O2StationarySpells.addStationarySpell: adding " + spell.getSpellType().toString() + " with duration " + spell.duration + " and radius of " + spell.radius, null, null, false);
      O2StationarySpells.add(spell);
   }

   /**
    * Remove a stationary spell. Since we do not want to lose track of a spell, we set kill to true and let
    * upkeep() clean it up.
    *
    * @param spell the stationary spell to remove
    */
   public void removeStationarySpell(@NotNull O2StationarySpell spell)
   {
      common.printDebugMessage("O2StationarySpells.removeStationarySpell: removing " + spell.getSpellType().toString(), null, null, false);
      spell.kill();
   }

   /**
    * Return a list of active stationary spells
    *
    * @return a list of active stationary spells
    */
   @NotNull
   public List<O2StationarySpell> getActiveStationarySpells ()
   {
      List<O2StationarySpell> active = new ArrayList<>();

      for (O2StationarySpell spell : O2StationarySpells)
      {
         if (!spell.kill && spell.active)
            active.add(spell);
      }

      return active;
   }

   /**
    * Checks if the location is within one or more stationary spell objects, regardless of whether or not they are active.
    *
    * @param location - location to check
    * @return List of StationarySpellObj that the location is inside
    */
   @NotNull
   public List<O2StationarySpell> getStationarySpellsAtLocation(@NotNull Location location)
   {
      List<O2StationarySpell> inside = new ArrayList<>();

      for (O2StationarySpell stationary : O2StationarySpells)
      {
         if (stationary.location.getWorld() == null || location.getWorld() == null)
            continue;

         if (stationary.location.getWorld().getUID().equals(location.getWorld().getUID()))
         {
            if (stationary.location.distance(location) < stationary.radius)
            {
               inside.add(stationary);
            }
         }
      }

      return inside;
   }

   /**
    * Get all active stationary spells of a specific type at the location
    *
    * @param location  the location
    * @param spellType the spell type
    * @return a list of spells of that type found at the location
    */
   @NotNull
   public List<O2StationarySpell> getActiveStationarySpellsAtLocationByType(@NotNull Location location, @NotNull O2StationarySpellType spellType)
   {
      List<O2StationarySpell> spells = getStationarySpellsAtLocation(location);
      List<O2StationarySpell> found = new ArrayList<>();

      for (O2StationarySpell spell : spells)
      {
         if (spell.getSpellType() == spellType && spell.active)
         {
            found.add(spell);
         }
      }

      return found;
   }

   /**
    * Check for a specific type of stationary spell at a location
    *
    * @param location            the location to check
    * @param stationarySpellType the stationary spell type to check for
    * @return true if spell of that type exists at that location, false otherwise
    */
   public boolean checkLocationForSpell(@NotNull Location location, @NotNull O2StationarySpellType stationarySpellType)
   {
      List<O2StationarySpell> spellsAtLocation = getStationarySpellsAtLocation(location);

      for (O2StationarySpell statSpell : spellsAtLocation)
      {
         if (statSpell.spellType == stationarySpellType)
            return true;
      }

      return false;
   }

   /**
    * Determine if the location is inside of a stationary spell area.
    *
    * @param stationarySpell the stationary spell to check
    * @param loc             the location to check
    * @return true if the location is inside this stationary spell, false otherwise
    */
   public boolean isInsideOf(@NotNull O2StationarySpellType stationarySpell, @NotNull Location loc)
   {
      for (O2StationarySpell spell : O2StationarySpells)
      {
         if (spell.getSpellType() == stationarySpell)
         {
            if (spell.isInside(loc) && !spell.kill && spell.active)
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Heartbeat actions for stationary spells:
    * - for active spells, run checkEffect()
    * - for inactive spells, clean them up
    */
   public void upkeep ()
   {
      List<O2StationarySpell> s = new ArrayList<>(O2StationarySpells);

      for (O2StationarySpell statSpell : s)
      {
         statSpell.checkEffect();

         if (statSpell.kill)
         {
            common.printDebugMessage("O2StationarySpells.upkeep: removing " + statSpell.getSpellType().toString(), null, null, false);

            O2StationarySpells.remove(statSpell);
         }
      }
   }

   /**
    * Save stationary spells to disk
    */
   public void saveO2StationarySpells ()
   {
      List <Map<String, String>> serializedList = serializeO2StationarySpells();

      GsonDAO gsonLayer = new GsonDAO();
      gsonLayer.writeSaveData(serializedList, GsonDAO.o2StationarySpellsJSONFile);
   }

   /**
    * Read stationary spells from disk
    */
   void loadO2StationarySpells ()
   {
      GsonDAO gsonLayer = new GsonDAO();
      List<Map<String, String>> serializedSpells = gsonLayer.readSavedDataListMap(GsonDAO.o2StationarySpellsJSONFile);

      if (serializedSpells == null)
      {
         common.printLogMessage("Unable to load saved stationary spells.", null, null, false);
      }
      else
      {
         common.printLogMessage("Reading saved stationary spells", null, null, false);
         O2StationarySpells = deserializeO2StationarySpells(serializedSpells);
      }
   }

   /**
    * Serialize stationary spells for writing out json
    *
    * @return a map of the serialized stationary spell data
    */
   @NotNull
   private List<Map<String, String>> serializeO2StationarySpells ()
   {
      List <Map<String, String>> serializedList = new ArrayList<>();

      common.printDebugMessage("Serializing O2StationarySpells...", null, null, false);

      for (O2StationarySpell spell : O2StationarySpells)
      {
         Map<String, String> spellData = new HashMap<>();

         //
         // Spell type
         //
         spellData.put(spellLabel, spell.getSpellType().toString());

         //
         // Player UUID
         //
         spellData.put(playerUUIDLabel, spell.playerUUID.toString());

         //
         // Location
         //
         Map<String, String> locData = Ollivanders2API.common.serializeLocation(spell.location, spellLocLabel);
         if (locData != null)
         {
            for (Entry<String, String> e : locData.entrySet())
            {
               spellData.put(e.getKey(), e.getValue());
            }
         }

         //
         // Duration
         //
         spellData.put(durationLabel, Integer.toString(spell.duration));

         //
         // Radius
         //
         spellData.put(radiusLabel, Integer.toString(spell.radius));

         serializedList.add(spellData);

         //
         // get spell-specific data
         //
         Map<String, String> uniqueData = spell.serializeSpellData();
         for (Entry<String, String> e : uniqueData.entrySet())
         {
            spellData.put(e.getKey(),e.getValue());
         }
      }

      return serializedList;
   }

   /**
    * Deserialize stationary spells
    */
   @NotNull
   private List<O2StationarySpell> deserializeO2StationarySpells(@NotNull List<Map<String, String>> serializedSpells)
   {
      List<O2StationarySpell> statSpells = new ArrayList<>();

      for (Map<String, String> spellData : serializedSpells)
      {
         //
         // spell name
         //
         if (!spellData.containsKey(spellLabel))
            continue;
         String name = spellData.get(spellLabel);
         O2StationarySpellType spellType = O2StationarySpellType.getStationarySpellTypeFromString(name);
         if (spellType == null)
            continue;

         O2StationarySpell statSpell = getStationarySpellByType(spellType);
         if (statSpell == null)
            continue;

         //
         // caster uuid
         //
         if (!spellData.containsKey(playerUUIDLabel))
            continue;
         UUID pid = Ollivanders2API.common.uuidFromString(spellData.get(playerUUIDLabel));
         if (pid == null)
            continue;
         statSpell.setPlayerID(pid);

         //
         // spell radius
         //
         if (!spellData.containsKey(radiusLabel))
            continue;
         Integer radius = Ollivanders2API.common.integerFromString(spellData.get(radiusLabel));
         if (radius == null)
            continue;
         statSpell.setRadius(radius);

         //
         // spell duration
         //
         if (!spellData.containsKey(durationLabel))
            continue;
         Integer duration = Ollivanders2API.common.integerFromString(spellData.get(durationLabel));
         if (duration == null)
            continue;
         statSpell.setDuration(duration);

         //
         // spell location
         //
         Location location = Ollivanders2API.common.deserializeLocation(spellData, spellLocLabel);
         if (location == null)
            continue;
         statSpell.setLocation(location);

         //
         // spell unique data
         //
         statSpell.deserializeSpellData(spellData);

         statSpell.setActive(true);
         statSpells.add(statSpell);
      }

      return statSpells;
   }

   /**
    * Create a basic stationary spell object by type. This will not have all the fields set to be active so
    * will be set inactive by default.
    *
    * @param spellType the type of spell to create
    * @return the spell if it could be created, null otherwise
    */
   @Nullable
   public O2StationarySpell getStationarySpellByType(@NotNull O2StationarySpellType spellType)
   {
      O2StationarySpell statSpell;

      Class<?> spellClass = spellType.getClassName();
      try
      {
         statSpell = (O2StationarySpell)spellClass.getConstructor(Ollivanders2.class).newInstance(p);
      }
      catch (Exception e)
      {
         common.printDebugMessage("Exception trying to create new instance of " + spellType.toString(), e, null, true);
         return null;
      }

      return statSpell;
   }
}
