package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hurts any entities within 0.5 meters of the spell wall.
 *
 * @author lownes
 */
public class PROTEGO_MAXIMA extends ShieldSpell implements StationarySpell
{
   double damage;

   private final String damageLabel = "Damage";

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PROTEGO_MAXIMA (Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PROTEGO_MAXIMA;
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
    * @param damage the damage done to other entities in this spell area
    */
   public PROTEGO_MAXIMA (Ollivanders2 plugin, UUID pid, Location location, O2StationarySpellType type, Integer radius,
                          Integer duration, double damage)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.PROTEGO_MAXIMA;
      this.damage = damage;
   }

   @Override
   public void checkEffect ()
   {
      age();

      Collection<LivingEntity> nearbyEntities = Ollivanders2API.common.getLivingEntitiesInRadius(location, radius + 1);

      for (LivingEntity e : nearbyEntities)
      {
         if (e instanceof Player)
         {
            Player ply = (Player) e;
            if (ply.isPermissionSet("Ollivanders2.BYPASS"))
            {
               if (ply.hasPermission("Ollivanders2.BYPASS"))
               {
                  continue;
               }
            }
         }
         double distance = e.getLocation().distance(location);
         if (distance > radius - 0.5 && distance < radius + 0.5)
         {
            e.damage(damage);

            flair(10);
         }
      }
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

      spellData.put(damageLabel, Double.toString(damage));

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
      for (Map.Entry<String, String> e : spellData.entrySet())
      {
         try
         {
            if (e.getKey().equals(damageLabel))
            {
               damage = Double.parseDouble(e.getValue());
            }
         }
         catch (Exception exception)
         {
            p.getLogger().info("Unable to read Protego Maxima damage");
            if (Ollivanders2.debug)
               exception.printStackTrace();
         }
      }
   }
}