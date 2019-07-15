package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public abstract class O2EffectOnSelf extends Charms
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
    * The effects to add.
    */
   ArrayList<O2EffectType> effectsToAdd = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public O2EffectOnSelf ()
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
   public O2EffectOnSelf (Ollivanders2 plugin, Player player, Double rightWand)
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
      int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

      for (O2EffectType effectType : effectsToAdd)
      {
         Class effectClass = effectType.getClassName();

         O2Effect effect;
         try
         {
            effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, Integer.class, UUID.class).newInstance(p, duration, player.getUniqueId());
         }
         catch (Exception e)
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("Failed to create class for " + effectType.toString());
               e.printStackTrace();
            }
            continue;
         }

         Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
      }

      kill();
   }
}
