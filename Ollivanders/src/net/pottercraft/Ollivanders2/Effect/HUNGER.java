package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Give a player hunger
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HUNGER extends PotionEffectSuper
{
   int strength = 1;

   public HUNGER (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.HUNGER;
      potionEffectType = PotionEffectType.HUNGER;
      informousText = legilimensText = "is hungry";

      divinationText.add("shall be struck by a terrible affliction");
      divinationText.add("will starve");
      divinationText.add("shall be cursed");
      divinationText.add("will become insatiable");
   }
}