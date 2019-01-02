package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.UUID;

/**
 * This effect keeps a player awake when other effects would cause them to sleep.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class AWAKE extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the player this effect acts on
    */
   public AWAKE (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.AWAKE;
      informousText = legilimensText = "is unnaturally alert";
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
