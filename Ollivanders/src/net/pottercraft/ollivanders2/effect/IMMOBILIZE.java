package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Prevents a player from moving in any way.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class IMMOBILIZE extends O2Effect
{
   public IMMOBILIZE(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.IMMOBILIZE;
      informousText = legilimensText = "is unable to move";

      divinationText.add("will be possessed by a demon spirit");
      divinationText.add("will succomb to a primal fear");
      divinationText.add("shall become as if frozen");
      divinationText.add("shall be struck by a terrible affliction");
      divinationText.add("shall be cursed");
   }

   /**
    * Age this effect by 1, move the player up 1.5 blocks off the ground if they are not already suspended.
    */
   @Override
   public void checkEffect ()
   {
      age(1);
   }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove () { }
}
