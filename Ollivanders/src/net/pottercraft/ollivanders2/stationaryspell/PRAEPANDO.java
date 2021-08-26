package net.pottercraft.ollivanders2.stationaryspell;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.jetbrains.annotations.NotNull;

/**
 * Create a pocket of extra-dimensional space.
 *
 */
public class PRAEPANDO extends ExtraDimensional
{
   final private Set<UUID> teleported = new HashSet<>();

   private final String radiusLabel = "Radius";

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PRAEPANDO (Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PRAEPANDO;
   }

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param pid the player who cast the spell
    * @param location the center location of the spell
    * @param type the type of this spell
    * @param radius the radius for this spell
    * @param duration the duration of the spell
    * @param dimenRadius the size of the extra-dimensional space
    */
   public PRAEPANDO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration, int dimenRadius)
   {
      super(plugin, pid, location, type, radius, duration, dimenRadius);

      spellType = O2StationarySpellType.PRAEPANDO;
   }


   @Override
   public void checkEffect ()
   {
      Location edLocation = getEDLoc().clone().add(0, 1.1, 0);
      Location normLocation = location;
      World edWorld = edLocation.getWorld();
      World normWorld = normLocation.getWorld();

      if (edWorld == null || normWorld == null)
      {
         p.getLogger().warning("PRAEPENDO.checkEffect: world is null");
         kill();
         return;
      }

      edWorld.playEffect(edLocation, Effect.MOBSPAWNER_FLAMES, 0);
      edWorld.playEffect(normLocation, Effect.MOBSPAWNER_FLAMES, 0);

      for (Entity entity : normWorld.getEntities())
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
         if (sp.getTargetBlock().equals(edLocation.getBlock()))
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
   @NotNull
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData)
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
