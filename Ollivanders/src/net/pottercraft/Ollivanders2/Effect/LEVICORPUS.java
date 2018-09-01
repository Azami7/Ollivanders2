package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Keeps a player hoisted 1.5 blocks into the air
 *
 * @author lownes
 */
public class LEVICORPUS extends O2Effect
{
   Location loc;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public LEVICORPUS (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
   {
      super(plugin, effect, duration, player);

      this.loc = player.getEyeLocation();
   }

   /**
    * Age this effect by 1, move the player up 1.5 blocks off the ground
    */
   @Override
   public void checkEffect ()
   {
      age(1);
      target.setAllowFlight(duration > 1);
      Location curLoc = target.getLocation();
      Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), curLoc.getYaw(), 90);
      target.teleport(newLoc);
   }
}
