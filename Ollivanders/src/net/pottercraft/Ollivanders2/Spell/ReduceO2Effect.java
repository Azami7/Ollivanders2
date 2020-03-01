package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Reduce duration of an O2Effect from a target player.
 *
 * @author Azami7
 */
public class ReduceO2Effect extends O2Spell
{
   /**
    * Number of targets that can be affected
    */
   int numberOfTargets = 1;

   /**
    * The effects to add.
    */
   ArrayList<O2EffectType> effectsToReduce = new ArrayList<>();

   /**
    * Blacklist of effects that cannot be removed by a spell.
    */
   ArrayList<O2EffectType> effectBlacklist = new ArrayList<O2EffectType>()
   {{
      add(O2EffectType.ANIMAGUS_EFFECT);
      add(O2EffectType.ANIMAGUS_INCANTATION);
      add(O2EffectType.LYCANTHROPY);
      add(O2EffectType.LYCANTHROPY_SPEECH);
      add(O2EffectType.LYCANTHROPY_RELIEF);
   }};

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ReduceO2Effect()
   {
      super();

      branch = O2MagicBranch.CHARMS;
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ReduceO2Effect(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.CHARMS;
   }

   /**
    * If a target player is within the radius of the projectile, add the potion effect to the player.
    */
   @Override
   protected void doCheckEffect()
   {
      affectRadius(1.5, false);

      if (hasHitTarget())
      {
         kill();
      }
   }

   /**
    * Affect targets within the radius.
    *
    * @param radius the radius of the spell
    * @param flair  whether or not to show a visual flair
    */
   void affectRadius(double radius, boolean flair)
   {
      if (flair)
      {
         Ollivanders2Common.flair(location, (int) radius, 10);
      }

      for (LivingEntity livingEntity : getLivingEntities(radius))
      {
         if ((livingEntity.getUniqueId() == player.getUniqueId()) || !(livingEntity instanceof Player))
            continue;

         reducePotionEffects((Player) livingEntity);

         numberOfTargets--;

         // stop when the limit of targets is reached
         if (numberOfTargets <= 0)
         {
            kill();
            return;
         }
      }
   }

   /**
    * Reduce duration of O2Effects from the target player
    *
    * @param target the player to remove effects from
    */
   void reducePotionEffects(Player target)
   {
      for (O2EffectType effectType : effectsToReduce)
      {
         if (effectBlacklist.contains(effectType))
            continue;

         if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), effectType))
         {
            Ollivanders2API.getPlayers().playerEffects.ageEffect(player.getUniqueId(), effectType, (int) (usesModifier * 2400));
         }
      }
   }
}
