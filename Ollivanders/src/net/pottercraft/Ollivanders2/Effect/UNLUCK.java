package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Make a player unlucky
 *
 * @author Azami7
 * @since 2.2.9
 */
public class UNLUCK extends PotionEffectSuper
{
   int strength = 1;

   public UNLUCK (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.UNLUCK;
      potionEffectType = PotionEffectType.UNLUCK;
      informousText = legilimensText = "feels unlucky";

      divinationText.add("will be cursed by misfortune");
      divinationText.add("shall be cursed");
      divinationText.add("will find nothing but misfortune");
   }
}
