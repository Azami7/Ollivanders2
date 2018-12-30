package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class for managing all prophecies. A prophecy is made via divination and concerns future effects on a player.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class Prophecies
{
   Ollivanders2 p;

   /**
    * Class representing a prophecy. Every prophecy involves predicting an effect on a player in the future and
    * has a specific accuracy which changes the likelihood the prophecy will come to pass.
    */
   private class Prophecy
   {
      /**
       * The effect that will happen to the player
       */
      O2EffectType effect;

      /**
       * The player that will be affected
       */
      UUID targetID;

      /**
       * The time until the prophecy will come to pass, in game ticks - one game tick is 1/20 of a second
       */
      Integer time;

      /**
       * The duration, in game ticks, for this prophecy
       */
      Integer duration;

      /**
       * The percent accuracy of this prophecy
       */
      Integer accuracy;

      /**
       * Constructor
       *
       * @param e  the effect that will happen to the player
       * @param id the id of the player that will be affected
       * @param s  the time, in seconds, until the prophecy will come to pass
       * @param d  the duration of the effect, 0 for permanent
       * @param a  the accuracy of this prophecy as a percent from 0 to 99, greater than 99 will be rounded down to 99
       */
      public Prophecy (O2EffectType e, UUID id, int s, int d, int a)
      {
         effect = e;
         targetID = id;
         time = s * 20;
         duration = d;
         accuracy = a;
      }

      public O2EffectType getEffect ()
      {
         return effect;
      }

      public UUID getTargetID ()
      {
         return targetID;
      }

      public Integer getTime ()
      {
         return time;
      }

      public Integer getDuration ()
      {
         return duration;
      }
   }

   ArrayList<Prophecy> prophecies = new ArrayList<>();

   public Prophecies (Ollivanders2 plugin)
   {
      p = plugin;
   }

   public void upkeep ()
   {
      for (Prophecy prophecy : prophecies)
      {

      }
   }
}
