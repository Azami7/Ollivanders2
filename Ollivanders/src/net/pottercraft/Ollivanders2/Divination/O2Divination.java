package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class O2Divination
{
   Ollivanders2 p;

   O2DivinationType divintationType = O2DivinationType.ASTROLOGY;

   int maxAccuracy = 10;

   Material itemHeld = null;
   Material itemNearby = null;

   Player target;
   Player prophet;
   int experience;

   ArrayList<String> prophecyPrefix = new ArrayList<>();

   public static final ArrayList<O2EffectType> divinationEffects = new ArrayList<O2EffectType>()
   {{
      add(O2EffectType.SLEEPING);
      add(O2EffectType.BABBLING);
      add(O2EffectType.AGGRESSION);
      add(O2EffectType.IMMOBILIZE);
      add(O2EffectType.MUTED_SPEECH);
   }};

   public O2Divination (Ollivanders2 plugin, Player pro, Player tar, Integer exp)
   {
      p = plugin;
      target = tar;
      prophet = pro;
      experience = exp;
   }

   /**
    * Make a prophecy by the prophet about the target.
    * <p>
    * Parts of a prophecy:
    * <p>
    * 1. prophecyPrefix - optional prefix based on the specific divination method, such as "Due to the influence of Mars"
    * 2. the time of day - midnight, sunrise, midday, sunset
    * 3. the day - today, tomorrow, 3rd day, 4th day
    * 4. the target's name
    * 5. what will happen to them - see
    * A prophecy should read like:
    * "After the sun sets on the 3rd day, Fred will fall in to a deep sleep."
    */
   public void divine ()
   {
      prophet.sendMessage(Ollivanders2.chatColor + "You make a prophecy about " + target.getName() + " using " + p.common.enumRecode(divintationType.toString()));
      target.sendMessage(Ollivanders2.chatColor + prophet.getName() + " makes a prophecy about you using " + p.common.enumRecode(divintationType.toString()));

      //
      // first, determine the success odds of this prophecy
      //
      // Calculation:
      // the prophet gets a 4% chance per level of experience at this type of divination
      // the type of divination method has a maximum accuracy level, regardless of skill
      //
      int accuracy = 4 * experience;
      if (accuracy > maxAccuracy)
      {
         accuracy = maxAccuracy;
      }
   }
}
