package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Gives a player more spell experience while effect is active.
 *
 * @since 1.0
 */
public class BARUFFIOS_BRAIN_ELIXIR extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public BARUFFIOS_BRAIN_ELIXIR (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
   {
      super(plugin, effect, duration, player);
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
