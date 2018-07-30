package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;

public class O2StationarySpells
{
   private List<StationarySpellObj> O2StationarySpells = new ArrayList<>();
   Ollivanders2 p;

   /**
    * Constructor
    *
    * @param plugin
    */
   public O2StationarySpells (Ollivanders2 plugin)
   {
      p = plugin;
      loadO2StationarySpells();
   }

   /**
    * Add a stationary spell
    *
    * @param spell
    */
   public void addStationarySpell (StationarySpellObj spell)
   {
      if (spell != null)
         O2StationarySpells.add(spell);
   }

   /**
    * Remove a stationary spell. Since we do not want to lose track of a spell, we set kill to true and let
    * upkeep() clean it up.
    *
    * @param spell
    */
   public void removeStationarySpell (StationarySpellObj spell)
   {
      spell.kill();
   }

   /**
    * Return a list of active stationary spells
    *
    * @return
    */
   public List<StationarySpellObj> getActiveStationarySpells ()
   {
      List<StationarySpellObj> active = new ArrayList<>();

      for (StationarySpellObj spell : O2StationarySpells)
      {
         if (spell.active)
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
   public List<StationarySpellObj> getStationarySpellsAtLocation (Location location)
   {
      List<StationarySpellObj> inside = new ArrayList<>();
      for (StationarySpellObj stationary : O2StationarySpells)
      {
         if (stationary.location.getWorldUUID().equals(location.getWorld().getUID()))
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
    * Determine if the location is inside of a stationary spell area.
    *
    * @param stationarySpell
    * @param loc
    * @return
    */
   public boolean isInsideOf (StationarySpells stationarySpell, Location loc)
   {
      for (StationarySpellObj spell : O2StationarySpells)
      {
         if (spell.name == stationarySpell)
         {
            if (spell.isInside(loc) && spell.active)
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
      for (StationarySpellObj spell : O2StationarySpells)
      {
         if (spell.active)
         {
            ((StationarySpell)spell).checkEffect(p);
         }
         else
         {
            spell.kill();
         }

         if (spell.kill)
         {
            O2StationarySpells.remove(spell);
         }
      }
   }

   /**
    * Save stationary spells to disk
    */
   public void saveO2StationarySpells ()
   {
      try
      {
         Ollivanders2.SLAPI.save(O2StationarySpells, "plugins/Ollivanders2/stationary.bin");
         p.getLogger().finest("Saved stationary.bin");
      }
      catch (Exception e)
      {
         p.getLogger().warning("Could not save stationary.bin");
      }

      //TODO make this serialize to json rather than binary
   }

   /**
    * Read stationary spells from disk
    */
   void loadO2StationarySpells ()
   {
      try
      {
         O2StationarySpells = (List<StationarySpellObj>) Ollivanders2.SLAPI.load("plugins/Ollivanders2/stationary.bin");
         p.getLogger().info("Loaded save file stationary.bin");
      }
      catch (Exception e)
      {
         p.getLogger().warning("Did not find stationary.bin");
      }

      //TODO make this serialize to json rather than binary
   }
}
