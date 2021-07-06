package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the player this effect acts on
    */
   public SLEEPING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.SLEEPING;
      informousText = legilimensText = "is affected by an unnatural sleep";

      divinationText.add("will fall silent");
      divinationText.add("shall fall in to a deep sleep");
      divinationText.add("shall pass beyond this realm");
      divinationText.add("will surrender to a sleeping death");

      permanent = false;
   }

   @Override
   public void checkEffect ()
   {
      if (!sleeping)
      {
         if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(targetID, O2EffectType.AWAKE))
         {
            kill();
         }
         else
         {
            playerSleep();
         }
      }

      if (!permanent)
      {
         age(1);
      }
   }

   @Override
   public void kill ()
   {
      if (sleeping)
      {
         playerWake();
      }

      kill = true;
   }

   /**
    * Put the player to sleep.
    */
   private void playerSleep ()
   {
      Player target = p.getServer().getPlayer(targetID);
      if (target == null)
      {
         kill();
         return;
      }

      // tilt player head down
      Location loc = target.getLocation();
      Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 45);
      target.teleport(newLoc);

      // add sleep speech
      SLEEP_SPEECH effect = new SLEEP_SPEECH(p, -1, targetID);

      Ollivanders2API.getPlayers(p).playerEffects.addEffect(effect);

      sleeping = true;
   }

   /**
    * Wake the player up.
    */
   private void playerWake ()
   {
      Ollivanders2API.getPlayers(p).playerEffects.removeEffect(targetID, O2EffectType.SLEEP_SPEECH);
      sleeping = false;

      Player target = p.getServer().getPlayer(targetID);
      if (target != null)
         target.sendMessage(Ollivanders2.chatColor + "You awaken from a deep sleep.");
      else
         kill();
   }
}
