package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Keeps a player hoisted 1.5 blocks into the air
 *
 * @author lownes
 */
public class LEVICORPUS extends OEffect implements Effect
{
   Location loc;

   public LEVICORPUS (Player sender, Effects effect, int duration, Location loc)
   {
      super(sender, effect, duration);
      this.loc = loc;
   }

   @Override
   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
      owner.setAllowFlight(duration > 1);
      Location curLoc = owner.getLocation();
      Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), curLoc.getYaw(), 90);
      owner.teleport(newLoc);
   }
}
