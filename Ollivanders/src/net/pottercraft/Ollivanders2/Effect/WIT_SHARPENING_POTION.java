package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Makes book learning more effective.
 *
 * @author Azami7
 */
public class WIT_SHARPENING_POTION extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    */
   public WIT_SHARPENING_POTION(Ollivanders2 plugin, O2EffectType effect, int duration)
   {
      super(plugin, effect, duration);
   }

   /**
    * Age this effect each game tick.
    *
    * @param target the player affected by the effect
    */
   @Override
   public void checkEffect (Player target)
   {
      age(1);
   }
}
