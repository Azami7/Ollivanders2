package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Harm a player
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HARM extends PotionEffectSuper
{
   public HARM(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.HARM;
      potionEffectType = PotionEffectType.INSTANT_DAMAGE;
      informousText = legilimensText = "feels unwell";

      strength = 1;

      divinationText.add("shall be struck by a terrible affliction");
      divinationText.add("will come to harm");
      divinationText.add("shall be cursed");
      divinationText.add("will be develop a terrible illness");
   }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove () { }
}