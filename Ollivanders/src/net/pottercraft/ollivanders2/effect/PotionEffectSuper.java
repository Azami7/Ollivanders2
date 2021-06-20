package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

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

   /**
    * Constructor
    *
    * @param plugin   a reference to the plugin
    * @param duration the duration of this effect
    * @param pid      the ID of the affected player
    */
   public PotionEffectSuper(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);
   }

   /**
    * Do the effect immediately on the target and kill.
    */
   @Override
   public void checkEffect()
   {
      Player target = p.getServer().getPlayer(targetID);

      if (target != null)
      {
         // base time is 2 minutes
         int base_time = 2400;
         int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 5) + 1;

         target.addPotionEffect(new PotionEffect(potionEffectType, base_time * rand, strength));
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
