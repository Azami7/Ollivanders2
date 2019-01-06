package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Make a player blind
 *
 * @author Azami7
 * @since 2.2.9
 */
public class BLINDNESS extends PotionEffectSuper
{
   int strength = 1;

   public BLINDNESS (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.LUCK;
      potionEffectType = PotionEffectType.LUCK;
      informousText = legilimensText = "cannot see";

      divinationText.add("shall be cursed");
      divinationText.add("shall be afflicted in the mind");
      divinationText.add("will lose their mind to insanity");
      divinationText.add("will be struck by a terrible affliction");
   }
}