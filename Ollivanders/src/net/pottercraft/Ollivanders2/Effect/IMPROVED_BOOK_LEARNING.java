package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Makes book learning more effective.
 *
 * @author Azami7
 */
public class IMPROVED_BOOK_LEARNING extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public IMPROVED_BOOK_LEARNING (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.IMPROVED_BOOK_LEARNING;
   }

   /**
    * Age this effect each game tick.
    */
   @Override
   public void checkEffect ()
   {
      age(1);
   }
}