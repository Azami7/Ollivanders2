package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

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
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public LEVICORPUS (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.LEVICORPUS;

      Player target = p.getServer().getPlayer(targetID);
      this.loc = target.getEyeLocation();
   }

   /**
    * Age this effect by 1, move the player up 1.5 blocks off the ground
    */
   @Override
   public void checkEffect ()
   {
      age(1);

      Player target = p.getServer().getPlayer(targetID);
      target.setAllowFlight(duration > 1);
      Location curLoc = target.getLocation();
      Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), curLoc.getYaw(), 90);
      target.teleport(newLoc);
   }
}