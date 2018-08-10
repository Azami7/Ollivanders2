package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Player will spawn here when killed, with all of their spell levels intact. Only fiendfyre can destroy it.
 *
 * @author lownes
 */
public class HORCRUX extends StationarySpellObj implements StationarySpell
{
   public HORCRUX (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public HORCRUX (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                   Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   public void checkEffect ()
   {
      List<LivingEntity> entities = getLivingEntities();
      for (LivingEntity entity : entities)
      {
         if (entity instanceof Player)
         {
            if (entity.getUniqueId() != getPlayerUUID())
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