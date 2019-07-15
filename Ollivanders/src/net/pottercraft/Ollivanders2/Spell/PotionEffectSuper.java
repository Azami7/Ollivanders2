package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public abstract class PotionEffectSuper extends Charms
{
   /**
    * The duration for this effect.
    */
   int durationInSeconds;

   /**
    * The longest this effect can last.
    */
   int maxDurationInSeconds = 300; // 5 minutes;

   /**
    * The least amount of time this effect can last.
    */
   int minDurationInSeconds = 5; // 5 seconds

   /**
    * Strength modifier, 0 is no modifier.
    */
   int strengthModifier = 0;

   /**
    * Number of targets that can be affected
    */
   int numberOfTargets = 1;

   /**
    * The potion effect. Set to luck by default.
    */
   ArrayList<PotionEffectType> effectTypes = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PotionEffectSuper ()
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
   public PotionEffectSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      durationInSeconds = minDurationInSeconds;
   }

   /**
    * If a target player is within the radius of the projectile, add the potion effect to the player.
    */
   @Override
   protected void doCheckEffect ()
   {
      for (LivingEntity livingEntity : getLivingEntities(1.5))
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
            kill();
            return;
         }
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}
