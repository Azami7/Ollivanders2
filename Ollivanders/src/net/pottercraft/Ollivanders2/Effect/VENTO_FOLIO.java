package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;

/**
 * Grants player flight
 *
 * @author lownes
 */
public class VENTO_FOLIO extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    */
   public VENTO_FOLIO (Ollivanders2 plugin, O2EffectType effect, int duration)
   {
      super(plugin, effect, duration);
   }

   /**
    * Allow player to fly until the effect ends.
    *
    * @param target the player affected by the effect
    */
   @Override
   public void checkEffect (Player target)
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
