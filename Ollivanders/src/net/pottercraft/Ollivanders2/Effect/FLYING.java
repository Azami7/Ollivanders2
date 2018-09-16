package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

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
    * @param pid the ID of the player this effect acts on
    */
   public FLYING (Ollivanders2 plugin, O2EffectType effect, Integer duration, UUID pid)
   {
      super(plugin, effect, duration, pid);
   }

   /**
    * Allow player to fly until the effect ends.
    */
   @Override
   public void checkEffect ()
   {
      Player target = p.getServer().getPlayer(targetID);

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