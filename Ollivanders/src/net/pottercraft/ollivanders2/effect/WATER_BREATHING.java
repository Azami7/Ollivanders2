package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Gives a player water breathing
 *
 * @author Azami7
 * @since 2.2.9
 */
public class WATER_BREATHING extends PotionEffectSuper
{
   public WATER_BREATHING (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      strength = 1;

      effectType = O2EffectType.WATER_BREATHING;
      potionEffectType = PotionEffectType.WATER_BREATHING;
      informousText = legilimensText = "can breath in water";

      divinationText.add("will swim with the mermaids");
      divinationText.add("will feel fishy");
      divinationText.add("will no longer fear water");
   }
}
