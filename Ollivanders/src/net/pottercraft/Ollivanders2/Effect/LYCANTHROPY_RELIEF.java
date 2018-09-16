package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;
import net.pottercraft.Ollivanders2.Ollivanders2;

public class LYCANTHROPY_RELIEF extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public LYCANTHROPY_RELIEF (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.LYCANTHROPY_RELIEF;
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