package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Increases the experience a player gets for using a skill.
 *
 * @author Azami7
 */
public class FAST_LEARNING extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public FAST_LEARNING (Ollivanders2 plugin, O2EffectType effect, Integer duration, UUID pid)
   {
      super(plugin, effect, duration, pid);
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