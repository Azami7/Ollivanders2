package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectInRadiusSuper extends PotionEffectSuper
{
   /**
    * Radius of the spell from the caster.
    */
   int radius = 5;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PotionEffectInRadiusSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PotionEffectInRadiusSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      durationInSeconds = minDurationInSeconds;
   }

   /**
    * If a target player is within the radius of the projectile, add the potion effect to the player.
    */
   @Override
   public void checkEffect ()
   {
      if (!checkSpellAllowed())
      {
         kill();
         return;
      }

      Ollivanders2Common.flair(location, radius, 10);

      for (LivingEntity livingEntity : getLivingEntities(radius))
      {
         if (livingEntity.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

         for (PotionEffectType effectType : effectTypes)
         {
            PotionEffect effect = new PotionEffect(effectType, duration, strengthModifier);
            livingEntity.addPotionEffect(effect);
            numberOfTargets--;
         }

         // if the spell can only target a limited number, stop when the limit is reached
         if (numberOfTargets <= 0)
         {
            break;
         }
      }

      kill();
   }
}
