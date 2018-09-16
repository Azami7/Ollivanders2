package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;

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
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public ANIMAGUS_INCANTATION (Ollivanders2 plugin, O2EffectType effect, Integer duration, UUID pid)
   {
      super(plugin, effect, duration, pid);
   }

   /**
    * The animagus effect can only be changed/removed by the player repeating the incantation,
    * therefore there is nothing to do in checkEffect().
    */
   @Override
   public void checkEffect () { }
}