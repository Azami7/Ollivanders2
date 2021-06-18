package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Class representing a prophecy. Every prophecy involves predicting an effect on a player in the future and
 * has a specific accuracy which changes the likelihood the prophecy will come to pass.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class O2Prophecy
{
   final private Ollivanders2 p;

   /**
    * The effect that will happen to the player
    */
   private O2EffectType effectType;

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
   private long time;

   /**
    * The duration, in game ticks, for this prophecy
    */
   private int duration;

   /**
    * The percent accuracy of this prophecy
    */
   private int accuracy;

   /**
    * The message of this prophecy
    */
   private String prophecyMessage;

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
   O2Prophecy(@NotNull Ollivanders2 plugin, @NotNull O2EffectType e, @NotNull String m, @NotNull UUID tid, @NotNull UUID pid, long t, int d, int a)
   {
      p = plugin;
      effectType = e;
      targetID = tid;
      prophetID = pid;
      prophecyMessage = m;
      time = t;
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

   @NotNull
   public O2EffectType getEffect ()
   {
      return effectType;
   }

   @NotNull
   public UUID getTargetID ()
   {
      return targetID;
   }

   @NotNull
   UUID getProphetID ()
   {
      return prophetID;
   }

   public long getTime()
   {
      return time;
   }

   public int getDuration()
   {
      return duration;
   }

   String getProphecyMessage ()
   {
      return prophecyMessage;
   }

   int getAccuracy()
   {
      return accuracy;
   }

   boolean isKilled ()
   {
      return kill;
   }

   public void age()
   {
      time--;
   }

   public void kill()
   {
      kill = true;
   }

   /**
    * Execute this prophecy.
    */
   void fulfill()
   {
      if (Ollivanders2.debug)
      {
         p.getLogger().info("Fulfilling prophecy");
      }

      // this should only be called when the prophecy time has expired
      if (kill)
      {
         return;
      }

      Player target = p.getServer().getPlayer(targetID);

      if (target == null)
      {
         // player is offline, stash this prophecy for when the player returns
         Ollivanders2API.getProphecies(p).addOfflineProphecy(this);
         return;
      }

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

      if (accuracy > rand)
      {
         if (effectType != null)
         {
            O2Effect effect;
            Class<?> effectClass = effectType.getClassName();

            try
            {
               effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, duration, targetID);
            }
            catch (Exception e)
            {
               e.printStackTrace();
               kill();
               return;
            }

            effect.setPermanent(false);
            Ollivanders2API.getPlayers(p).playerEffects.addEffect(effect);

            O2Player player = Ollivanders2API.getPlayers(p).getPlayer(prophetID);
            if (player != null)
            {
               String playerName = player.getPlayerName();
               p.getServer().broadcastMessage(Ollivanders2.chatColor + "And so came to pass the prophecy of " + playerName + ", \"" + prophecyMessage + "\"");
            }
         }
         else
         {
            p.getLogger().info("Null effect in prophecy");
         }
      }
      else
      {
         Player prophet = p.getServer().getPlayer(prophetID);
         if (prophet != null)
         {
            prophet.sendMessage(Ollivanders2.chatColor + "Your prophecy, \"" + prophecyMessage + "\" did not come to pass.");
         }
      }

      kill();
   }
}
