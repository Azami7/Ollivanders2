package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Add a potion effect to a player.
 *
 * @author Azami7
 * @since 2.2.9
 */
public abstract class PotionEffectSuper extends O2Effect
{
   int strength = 1;
   PotionEffectType potionEffectType = PotionEffectType.GLOWING;

   public PotionEffectSuper (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);
   }

   /**
    * Age this effect each game tick.
    */
   @Override
   public void checkEffect ()
   {
      Player target = p.getServer().getPlayer(targetID);

      if (target != null)
      {
         // base time is 2 minutes
         int base_time = 2400;
         int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 5) + 1;

         target.addPotionEffect(new PotionEffect(potionEffectType, base_time * rand, strength), true);
      }

      kill();
   }

   /**
    * Set the strength of this effect
    *
    * @param s a positive integer where 1 is normal strength
    */
   public void setStrength (int s)
   {
      strength = s;
   }
}
