package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

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
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public IMPROVED_BOOK_LEARNING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.IMPROVED_BOOK_LEARNING;
      legilimensText = "feels more studious than usual";
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