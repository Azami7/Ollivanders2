package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Super class for all divination methods. Creates a prophecy which may come to pass at a future time.
 *
 * @author Azami7
 * @since 2.2.9
 */
public abstract class O2Divination
{
   Ollivanders2 p;

   O2DivinationType divintationType = O2DivinationType.ASTROLOGY;

   int maxAccuracy = 10;

   Player target;
   Player prophet;
   int experience;

   ArrayList<String> prophecyPrefix = new ArrayList<>();

   static final ArrayList<O2EffectType> divinationEffects = new ArrayList<O2EffectType>()
   {{
      add(O2EffectType.AGGRESSION);
      add(O2EffectType.BABBLING);
      add(O2EffectType.BLINDNESS);
      add(O2EffectType.CONFUSION);
      add(O2EffectType.IMMOBILIZE);
      add(O2EffectType.HARM);
      add(O2EffectType.HEAL);
      add(O2EffectType.HUNGER);
      add(O2EffectType.HEALTH_BOOST);
      add(O2EffectType.LUCK);
      add(O2EffectType.MUTED_SPEECH);
      add(O2EffectType.POISON);
      add(O2EffectType.SLEEPING);
      add(O2EffectType.SLOW);
      add(O2EffectType.UNLUCK);
      add(O2EffectType.WEAKNESS);
      add(O2EffectType.WEALTH);
   }};

   O2Divination (Ollivanders2 plugin, Player pro, Player tar, Integer exp)
   {
      p = plugin;
      target = tar;
      prophet = pro;
      experience = exp;

      prophecyPrefix.add("The portents and omens say that");
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
      UUID prophetID = prophet.getUniqueId();
      UUID targetID = target.getUniqueId();

      StringBuilder prophecyMessage = new StringBuilder();

      //
      // first, determine the accuracy of this prophecy
      //
      // Calculation:
      // - the prophet gets a 0.5% accuracy per level of experience at this type of divination
      // - the type of divination method has a maximum accuracy level, regardless of skill, which caps accuracy
      //
      int accuracy = experience / 2;
      if (accuracy > maxAccuracy)
      {
         accuracy = maxAccuracy;
      }

      //
      // second, pick the effect
      //
      int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % divinationEffects.size());
      O2EffectType effectType = divinationEffects.get(rand);

      O2Effect effect = getEffect(targetID, effectType);
      if (effect == null)
      {
         return;
      }

      if (prophecyPrefix.size() > 0)
      {
         rand = (Math.abs(Ollivanders2Common.random.nextInt()) % prophecyPrefix.size());
         prophecyMessage.append(prophecyPrefix.get(rand)).append(" ");
      }

      //
      // finally, the time of day and duration - via a lot of random chance
      //
      rand = (Math.abs(Ollivanders2Common.random.nextInt()) % Ollivanders2Common.TimeOfDay.values().length);
      Ollivanders2Common.TimeOfDay timeOfDay = Ollivanders2Common.TimeOfDay.values()[rand];

      prophecyMessage.append("at ").append(timeOfDay.toString().toLowerCase()).append(" ");

      rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 4);
      long curTime = target.getWorld().getTime();
      long ticks = 24000 - curTime;

      if (rand == 0)
      {
         //tomorrow
         prophecyMessage.append("tomorrow, ");
      }
      else if (rand == 1)
      {
         //tomorrow
         prophecyMessage.append("in two days, ");
         ticks = ticks + 24000;
      }
      else if (rand == 2)
      {
         //3rd day
         prophecyMessage.append("in three days, ");
         ticks = ticks + 48000;
      }
      else
      {
         //4th day
         prophecyMessage.append("in four days, ");
         ticks = ticks + 72000;
      }

      ticks = ticks + timeOfDay.getTick();

      //
      // duration (min 30 seconds, max 10 minutes)
      //
      int duration = 120 * experience;
      if (duration > 12000)
      {
         duration = 12000;
      }
      else if (duration < 600)
      {
         duration = 600;
      }

      //
      // finish prophecy
      //
      prophecyMessage.append(target.getName()).append(" ").append(effect.getDivinationText()).append(".");
      String finalMessage = prophecyMessage.toString();

      prophet.chat(finalMessage);
      O2Prophecy prophecy = new O2Prophecy(p, effectType, finalMessage, targetID, prophetID, ticks, duration, accuracy);
      Ollivanders2API.getProphecies().addProphecy(prophecy);
   }

   private O2Effect getEffect (UUID targetID, O2EffectType effectType)
   {
      Class<?> effectClass = effectType.getClassName();
      O2Effect effect;

      try
      {
         effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, Integer.class, UUID.class).newInstance(p, 1, targetID);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return null;
      }

      return effect;
   }
}
