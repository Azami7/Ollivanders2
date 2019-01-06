package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Make a player lucky
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

        effectType = O2EffectType.LUCK;
        potionEffectType = PotionEffectType.CONFUSION;
        informousText = legilimensText = "feels confused";

        divinationText.add("will be blessed by fortune");
        divinationText.add("will have unnatural luck");
        divinationText.add("shall find success in everything they do");
        divinationText.add("will become infallible");
    }
}