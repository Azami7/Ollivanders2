package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Make a player weak
 *
 * @author Azami7
 * @since 2.2.9
 */
public class WEAKNESS extends PotionEffectSuper
{
    int strength = 1;

    public WEAKNESS(Ollivanders2 plugin, Integer duration, UUID pid)
    {
        super(plugin, duration, pid);

        effectType = O2EffectType.WEAKNESS;
        potionEffectType = PotionEffectType.WEAKNESS;
        informousText = legilimensText = "feels weak";

        divinationText.add("shall be cursed");
        divinationText.add("will be cursed by weakness");
        divinationText.add("will be struck by a terrible affliction");
    }
}