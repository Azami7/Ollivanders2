package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Make a player slow
 *
 * @author Azami7
 * @since 2.2.9
 */
public class SLOWNESS extends PotionEffectSuper
{
    int strength = 1;

    public SLOWNESS(Ollivanders2 plugin, Integer duration, UUID pid)
    {
        super(plugin, duration, pid);

        effectType = O2EffectType.SLOWNESS;
        potionEffectType = PotionEffectType.SLOW;
        informousText = legilimensText = "feels confused";

        divinationText.add("shall be cursed");
        divinationText.add("will be afflicted in the mind");
        divinationText.add("will be struck by a terrible affliction");
        divinationText.add("will suffer a mental breakdown");
    }
}