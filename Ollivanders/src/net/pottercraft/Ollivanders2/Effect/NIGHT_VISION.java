package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Gives a player night vision
 *
 * @author Azami7
 * @since 2.2.9
 */
public class NIGHT_VISION extends PotionEffectSuper
{
   int strength = 1;

   public NIGHT_VISION (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.NIGHT_VISION;
      potionEffectType = PotionEffectType.NIGHT_VISION;
      informousText = legilimensText = "can breath in water";

      divinationText.add("will swim with the mermaids");
      divinationText.add("will feel fishy");
      divinationText.add("will no longer fear water");
   }
}
