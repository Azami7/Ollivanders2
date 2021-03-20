package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SPEED extends PotionEffectSuper
{
    public SPEED(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
    {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.SPEED;
        potionEffectType = PotionEffectType.SPEED;
        informousText = legilimensText = "is moving fast";

        divinationText.add("will make haste");
    }
}