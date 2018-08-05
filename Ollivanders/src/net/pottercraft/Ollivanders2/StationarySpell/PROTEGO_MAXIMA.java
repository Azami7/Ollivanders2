package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Spell.INCARNATIO_EQUUS;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.HashMap;
import java.util.Map;

/**
 * Hurts any entities within 0.5 meters of the spell wall.
 *
 * @author lownes
 */
public class PROTEGO_MAXIMA extends StationarySpellObj implements StationarySpell
{
   double damage;

   private final String damageLabel = "Damage";

   public PROTEGO_MAXIMA (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                          double damage)
   {
      super(player, location, name, radius, duration);
      this.damage = damage;
   }

   public PROTEGO_MAXIMA (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                          Map<String, String> spellData, Ollivanders2 plugin)
   {
      super(player, location, name, radius, duration);

      deserializeSpellData(spellData, plugin);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
      Location loc = location.toLocation();
      for (Entity e : loc.getWorld().getEntities())
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
         double distance = e.getLocation().distance(loc);
         if (distance > radius - 0.5 && distance < radius + 0.5)
         {
            if (e instanceof LivingEntity)
            {
               ((LivingEntity) e).damage(damage);
            }
            else
            {
               e.remove();
            }
            flair(10);
         }
      }
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @param p unused for this spell
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData (Ollivanders2 p)
   {
      Map<String, String> spellData = new HashMap<>();

      spellData.put(damageLabel, Double.toString(damage));

      return spellData;
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    * @param p unused for this spell
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData, Ollivanders2 p)
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