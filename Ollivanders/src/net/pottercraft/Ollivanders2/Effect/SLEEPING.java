package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Put the player to "sleep". Since we cannot actually force the player to do a sleep action, this
 * effect simulates sleep.
 *
 * @since 2.2.8
 * @author Azami7
 */
public class SLEEPING extends O2Effect
{
   boolean sleeping = false;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param pid the player this effect acts on
    */
   public SLEEPING (Ollivanders2 plugin, O2EffectType effect, Integer duration, UUID pid)
   {
      super(plugin, effect, duration, pid);

      permanent = false;
   }

   @Override
   public void checkEffect ()
   {
      if (!sleeping)
      {
         playerSleep();
      }
   }

   @Override
   public void kill ()
   {
      playerWake();

      kill = true;
   }

   /**
    * Make this effect permanent.
    */
   public void setPermanent ()
   {
      permanent = true;
   }

   /**
    * Put the player to sleep.
    */
   private void playerSleep ()
   {
      O2Player o2p = p.players.getPlayer(targetID);
      Player target = p.getServer().getPlayer(targetID);

      // tilt player head down
      Location loc = target.getLocation();
      Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 45);
      target.teleport(newLoc);

      // add sleep speech
      SLEEP_SPEECH effect = new SLEEP_SPEECH(p, O2EffectType.SLEEP_SPEECH, 5, targetID);
      p.players.playerEffects.addEffect(effect);

      sleeping = true;
   }

   /**
    * Wake the player up.
    */
   private void playerWake ()
   {
      O2Player o2p = p.players.getPlayer(targetID);

      p.players.playerEffects.removeEffect(targetID, O2EffectType.SLEEP_SPEECH);
      sleeping = false;
   }
}
