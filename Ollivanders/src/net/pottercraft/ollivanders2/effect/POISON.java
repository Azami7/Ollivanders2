package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Poison a player
 *
 * @author Azami7
 * @since 2.2.9
 */
public class POISON extends PotionEffectSuper
{
    public POISON (Ollivanders2 plugin, Integer duration, UUID pid)
    {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.POISON;
        potionEffectType = PotionEffectType.POISON;
        informousText = legilimensText = "feels sick";

        divinationText.add("will be struck by a terrible affliction");
        divinationText.add("shall come to harm");
        divinationText.add("will be cursed");
        divinationText.add("will be possessed by a demon spirit");
    }
}