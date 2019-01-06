package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Heal a player
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HEAL extends PotionEffectSuper
{
   int strength = 1;

   public HEAL (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.HEAL;
      potionEffectType = PotionEffectType.HEAL;
      informousText = legilimensText = "feels healthy";

      divinationText.add("will feel rejuvenated");
      divinationText.add("will be blessed by fortune");
      divinationText.add("shall be blessed");
   }
}