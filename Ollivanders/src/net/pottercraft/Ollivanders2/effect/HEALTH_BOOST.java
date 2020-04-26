package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Give a player a health boost
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HEALTH_BOOST extends PotionEffectSuper
{
    public HEALTH_BOOST (Ollivanders2 plugin, Integer duration, UUID pid)
    {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.HEALTH_BOOST;
        potionEffectType = PotionEffectType.HEALTH_BOOST;
        informousText = legilimensText = "feels stronger";

        divinationText.add("will become stonger");
        divinationText.add("will be blessed by fortune");
        divinationText.add("shall be blessed");
        divinationText.add("will rise to become more powerful");
    }
}