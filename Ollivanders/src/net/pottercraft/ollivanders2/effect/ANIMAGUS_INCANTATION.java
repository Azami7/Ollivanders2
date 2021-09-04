package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Sets that the player has an active Animagus incantion.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class ANIMAGUS_INCANTATION extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public ANIMAGUS_INCANTATION(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.ANIMAGUS_INCANTATION;
   }

   /**
    * The animagus effect can only be changed/removed by the player repeating the incantation,
    * therefore there is nothing to do in checkEffect().
    */
   @Override
   public void checkEffect () { }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove () { }
}