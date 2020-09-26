package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Make a player weak
 *
 * @author Azami7
 * @since 2.2.9
 */
public class WEAKNESS extends PotionEffectSuper
{
    public WEAKNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
    {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.WEAKNESS;
        potionEffectType = PotionEffectType.WEAKNESS;
        informousText = legilimensText = "feels weak";

        divinationText.add("shall be cursed");
        divinationText.add("will be cursed by weakness");
        divinationText.add("will be struck by a terrible affliction");
    }
}