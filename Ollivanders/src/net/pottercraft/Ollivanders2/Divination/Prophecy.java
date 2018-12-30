package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Class representing a prophecy. Every prophecy involves predicting an effect on a player in the future and
 * has a specific accuracy which changes the likelihood the prophecy will come to pass.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class Prophecy
{
   private Ollivanders2 p;

   /**
    * The effect that will happen to the player
    */
   private O2EffectType effect;

   /**
    * The player that will be affected
    */
   private UUID targetID;

   /**
    * The player that made the prophecy
    */
   private UUID prophetID;

   /**
    * The time until the prophecy will come to pass, in game ticks - one game tick is 1/20 of a second
    */
   private Integer time;

   /**
    * The duration, in game ticks, for this prophecy
    */
   private Integer duration;

   /**
    * The percent accuracy of this prophecy
    */
   private Integer accuracy;

   /**
    * Whether this prophecy has expired
    */
   private boolean kill = false;

   /**
    * Constructor
    *
    * @param e   the effect that will happen to the player
    * @param tid the id of the player that will be affected
    * @param pid the id of the player that made this prophecy
    * @param t   the time, in game ticks, until the prophecy will come to pass, less than 1200 (1 minute) will be rounded up to 1200
    * @param d   the duration of the effect, 0 for permanent
    * @param a   the accuracy of this prophecy as a percent from 0 to 99, greater than 99 will be rounded down to 99
    */
   public Prophecy (Ollivanders2 plugin, O2EffectType e, UUID tid, UUID pid, int t, int d, int a)
   {
      p = plugin;
      effect = e;
      targetID = tid;
      prophetID = pid;

      if (t < 1200)
      {
         time = 1200;
      }
      else
      {
         time = t;
      }

      duration = d;

      if (a > 99)
      {
         accuracy = 99;
      }
      else if (a < 0)
      {
         accuracy = 0;
      }
      else
      {
         accuracy = a;
      }
   }

   public O2EffectType getEffect ()
   {
      return effect;
   }

   public UUID getTargetID ()
   {
      return targetID;
   }

   public UUID getProphetID ()
   {
      return prophetID;
   }

   public Integer getTime ()
   {
      return time;
   }

   public Integer getDuration ()
   {
      return duration;
   }

   public boolean isKilled ()
   {
      return kill;
   }

   public void age ()
   {
      time--;
   }

   public void kill ()
   {
      kill = true;
   }

   public void fulfill ()
   {
      // this should only be called when the prophecy time has expired
      if (time > 0 || kill)
      {
         return;
      }

      int rand = Math.abs(Ollivanders2.random.nextInt() % 100);
      if (accuracy > rand)
      {
         if (effect != null)
         {
            Player target = p.getServer().getPlayer(targetID);
            Player prophet = p.getServer().getPlayer(prophetID);

            p.getServer().broadcastMessage("And so it came to pass that " + target.getName() + " was afflicted with " + p.common.enumRecode(effect.toString()) + " by the prophecy of " + prophet.getName());
         }
      }

      kill();
   }
}
