package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Make a player confused
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CONFUSION extends PotionEffectSuper
{
   int strength = 1;

   public CONFUSION (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.CONFUSION;
      potionEffectType = PotionEffectType.CONFUSION;
      informousText = legilimensText = "feels confused";

      divinationText.add("shall be cursed");
      divinationText.add("will be afflicted in the mind");
      divinationText.add("will be struck by a terrible affliction");
      divinationText.add("will suffer a mental breakdown");
   }
}