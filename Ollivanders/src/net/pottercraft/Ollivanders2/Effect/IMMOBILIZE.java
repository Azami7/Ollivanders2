package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.UUID;

/**
 * Prevents a player from moving in any way.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class IMMOBILIZE extends O2Effect
{
   public IMMOBILIZE (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.IMMOBILIZE;
      informousText = legilimensText = "is unable to move";
   }

   /**
    * Age this effect by 1, move the player up 1.5 blocks off the ground if they are not already suspended.
    */
   @Override
   public void checkEffect ()
   {
      age(1);
   }

   @Override
   public void kill ()
   {
      kill = true;
   }
}
