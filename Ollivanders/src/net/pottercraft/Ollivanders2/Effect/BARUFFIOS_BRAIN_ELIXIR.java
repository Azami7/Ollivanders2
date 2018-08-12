package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Gives a player more spell experience while effect is active.
 *
 * @since 1.0
 */
public class BARUFFIOS_BRAIN_ELIXIR extends OEffect implements Effect
{
   public BARUFFIOS_BRAIN_ELIXIR (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }
}
