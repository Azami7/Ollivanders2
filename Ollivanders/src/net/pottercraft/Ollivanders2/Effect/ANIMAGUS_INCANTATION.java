package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Sets that the player has an active Animagus incantion.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class ANIMAGUS_INCANTATION extends OEffect implements Effect
{
   /**
    * Constructor.
    *
    * @param sender
    * @param effect
    * @param duration
    */
   public ANIMAGUS_INCANTATION (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }
}
