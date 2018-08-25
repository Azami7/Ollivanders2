package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Sets that the player has an active Animagus incantion.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class ANIMAGUS_INCANTATION extends O2Effect
{
   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect on the player
    * @param duration the duration of this effect
    */
   public ANIMAGUS_INCANTATION (Ollivanders2 plugin, O2EffectType effect, int duration)
   {
      super(plugin, effect, duration);
   }

   /**
    * The animagus effect can only be changed/removed by the player repeating the incantation,
    * therefore there is nothing to do in checkEffect().
    *
    * @param target the player affected by the effect
    */
   @Override
   public void checkEffect (Player target) { }
}
