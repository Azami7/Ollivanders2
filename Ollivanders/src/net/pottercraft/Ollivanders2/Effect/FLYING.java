package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;

/**
 * Grants player flight
 *
 * @author lownes
 */
public class FLYING extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public FLYING (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
   {
      super(plugin, effect, duration, player);
   }

   /**
    * Allow player to fly until the effect ends.
    */
   @Override
   public void checkEffect ()
   {
      age(1);
      if (duration > 1)
      {
         target.setAllowFlight(true);
         target.getWorld().playEffect(target.getLocation(), org.bukkit.Effect.SMOKE, 4);
      }
      else
      {
         target.setAllowFlight(false);
      }
   }
}
