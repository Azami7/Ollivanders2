package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Player will spawn here when killed, with all of their spell levels intact.
 * Only fiendfyre can destroy it.
 *
 * @author lownes
 */
public class HORCRUX extends StationarySpellObj implements StationarySpell
{
   public HORCRUX (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
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
}