package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2Spell;

/**
 * Create a pocket of extra-dimensional space.
 *
 */
public class PRAEPANDO extends ExtraDimensional implements StationarySpell
{
   private Set<UUID> teleported = new HashSet<>();

   private final String radiusLabel = "Radius";

   public PRAEPANDO (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                     Integer dimenRadius)
   {
      super(plugin, player, location, name, radius, duration, dimenRadius);
   }

   public PRAEPANDO (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                     Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration, 1);

      deserializeSpellData(spellData);
   }

   @Override
   public void checkEffect ()
   {
      Location edLocation = getEDLoc().clone().add(0, 1.1, 0);
      Location normLocation = location;
      edLocation.getWorld().playEffect(edLocation, Effect.MOBSPAWNER_FLAMES, 0);
      normLocation.getWorld().playEffect(normLocation, Effect.MOBSPAWNER_FLAMES, 0);
      for (Entity entity : normLocation.getWorld().getEntities())
      {
         if (teleported.contains(entity.getUniqueId()))
         {
            if (entity.getLocation().distance(normLocation) > radius
                  && !entity.getLocation().getBlock().equals(edLocation.getBlock()))
            {
               teleported.remove(entity.getUniqueId());
            }
         }
         else
         {
            if (entity.getLocation().distance(normLocation) <= radius)
            {
               entity.teleport(edLocation);
               teleported.add(entity.getUniqueId());
            }
            else if (entity.getLocation().getBlock().equals(edLocation.getBlock()))
            {
               entity.teleport(normLocation);
               teleported.add(entity.getUniqueId());
            }
         }
      }
      for (O2Spell sp : p.getProjectiles())
      {
         if (sp.getBlock().equals(edLocation.getBlock()))
         {
            System.out.println("teleported projectile");
            sp.location = normLocation.clone();
         }
      }
      age();
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData ()
   {
      Map<String, String> spellData = new HashMap<>();

      spellData.put(radiusLabel, Integer.toString(radius));

      return spellData;
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData)
   {
      for (Entry<String, String> e : spellData.entrySet())
      {
         try
         {
            if (e.getKey().equals(radiusLabel))
               dimenRadius = Integer.parseInt(e.getValue());
         }
         catch (Exception exception)
         {
            p.getLogger().info("Unable to read Praependo radius");

            if (Ollivanders2.debug)
               exception.printStackTrace();
         }
      }
   }
}
