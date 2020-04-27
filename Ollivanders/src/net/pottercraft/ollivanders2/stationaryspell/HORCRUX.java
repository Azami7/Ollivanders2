package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Player will spawn here when killed, with all of their spell levels intact. Only fiendfyre can destroy it.
 *
 * @author lownes
 */
public class HORCRUX extends StationarySpellObj implements StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public HORCRUX (Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.HORCRUX;
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
    */
   public HORCRUX (Ollivanders2 plugin, UUID pid, Location location, O2StationarySpellType type, Integer radius,
                   Integer duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.HORCRUX;
   }

   public void checkEffect ()
   {
      List<LivingEntity> entities = getCloseLivingEntities();
      for (LivingEntity entity : entities)
      {
         if (entity instanceof Player)
         {
            if (entity.getUniqueId() != getCasterID())
            {
               Player player = (Player) entity;
               if (player.isPermissionSet("Ollivanders2.BYPASS"))
               {
                  if (player.hasPermission("Ollivanders2.BYPASS"))
                  {
                     continue;
                  }
               }
               PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 200, 2);
               PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 200, 3);
               entity.addPotionEffect(blindness);
               entity.addPotionEffect(wither);
            }
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
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData) { }
}