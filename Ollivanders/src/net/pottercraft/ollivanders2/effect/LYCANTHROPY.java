package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Turns player into a werewolf during the full moon. Doesn't go away until death (if deathExpLoss is set to
 * true).
 *
 * @author azami7
 * @since 2.2.8
 */
public class LYCANTHROPY extends ShapeShiftSuper
{
   ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public LYCANTHROPY(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.LYCANTHROPY;
      legilimensText = "is a werewolf";

      form = EntityType.WOLF;
      permanent = true;
      transformed = false;
   }

   /**
    * Transfigure the player back to human form and kill this effect.
    */
   @Override
   public void kill ()
   {
      restore();
      removeAdditionalEffect();

      kill = true;
   }

   /**
    * Change player in to a wolf for 1 day when the full moon occurs.
    *
    * See https://minecraft.gamepedia.com/Moon
    */
   @Override
   protected void upkeep ()
   {
      Player target = p.getServer().getPlayer(targetID);

      if (target == null)
      {
         kill();
         return;
      }

      long curTime = target.getWorld().getTime();
      if (!transformed)
      {
         // only need to check after sunset
         if (curTime > 13000)
         {
            long day = target.getWorld().getFullTime() / 24000;
            if ((day % 8) == 0)
            {
               // moonrise on a full moon day
               transform();

               addAdditionalEffects();

               target.playSound(target.getEyeLocation(), Sound.ENTITY_WOLF_HOWL, 1, 0);
            }
         }
      }
      else
      {
         long day = target.getWorld().getFullTime()/24000;
         boolean restore = false;

         if ((day % 8) == 0)
         {
            // if it is a full moon day before moonrise or after sunrise
            if (curTime < 13000 || curTime > 23500)
            {
               restore = true;
            }
         }
         else
         {
            // it is not a full moon day
            restore = true;
         }

         if (restore)
         {
            restore();
            removeAdditionalEffect();
         }
      }
   }

   /**
    * Add additional effects of lycanthropy such as aggression and speaking like a wolf
    */
   private void addAdditionalEffects ()
   {
      AGGRESSION aggression = new AGGRESSION(p, 5, targetID);
      aggression.setAggressionLevel(10);
      Ollivanders2API.getPlayers(p).playerEffects.addEffect(aggression);
      additionalEffects.add(O2EffectType.AGGRESSION);

      LYCANTHROPY_SPEECH speech = new LYCANTHROPY_SPEECH(p, 5, targetID);
      Ollivanders2API.getPlayers(p).playerEffects.addEffect(speech);
      additionalEffects.add(O2EffectType.LYCANTHROPY_SPEECH);
   }

   /**
    * Remove additional effects of Lycanthropy
    */
   private void removeAdditionalEffect()
   {
      for (O2EffectType effectType : additionalEffects)
      {
         Ollivanders2API.getPlayers(p).playerEffects.removeEffect(targetID, effectType);
      }
   }

   /**
    * Override setPermanent so that no code can inadvertently make lycanthropy effect age.
    *
    * @param perm true if this is permanent, false otherwise
    */
   @Override
   public void setPermanent(boolean perm) { }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove () { }
}