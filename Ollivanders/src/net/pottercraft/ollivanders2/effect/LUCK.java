package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Make a player lucky
 *
 * @author Azami7
 * @since 2.2.9
 */
public class LUCK extends PotionEffectSuper
{
   public LUCK(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      strength = 1;

      effectType = O2EffectType.LUCK;
      potionEffectType = PotionEffectType.LUCK;
      informousText = legilimensText = "feels lucky";

      divinationText.add("will be blessed by fortune");
      divinationText.add("will have unnatural luck");
      divinationText.add("shall find success in everything they do");
      divinationText.add("will become infallible");
   }
}
