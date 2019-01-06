package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Harm a player
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HARM extends PotionEffectSuper
{
    int strength = 1;

    public HARM (Ollivanders2 plugin, Integer duration, UUID pid)
    {
        super(plugin, duration, pid);

        effectType = O2EffectType.HARM;
        potionEffectType = PotionEffectType.HARM;
        informousText = legilimensText = "feels unwell";

        divinationText.add("shall be struck by a terrible affliction");
        divinationText.add("will come to harm");
        divinationText.add("shall be cursed");
        divinationText.add("will be develop a terrible illness");
    }
}