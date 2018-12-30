package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Effect.SLEEPING;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class O2Divination
{
   Ollivanders2 p;

   int minAccuracy;
   int maxAccuracy;

   Material itemHeld = null;
   Material itemNearby = null;

   Player target = null;
   Player prophet = null;

   String prophecyPrefix = null;

   public static final ArrayList<O2EffectType> divinationEffects = new ArrayList<O2EffectType>()
   {{
      add(O2EffectType.SLEEPING);
      add(O2EffectType.AWAKE);
      add(O2EffectType.BABBLING);
      add(O2EffectType.AGGRESSION);
   }};

   public O2Divination (Ollivanders2 plugin, int min, int max, Player pro, Player tar)
   {
      p = plugin;
      target = tar;
      prophet = pro;

      minAccuracy = min;
      maxAccuracy = max;
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

   }
}
