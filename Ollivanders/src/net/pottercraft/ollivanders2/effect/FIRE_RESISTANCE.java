package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FIRE_RESISTANCE extends PotionEffectSuper {
    public FIRE_RESISTANCE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FIRE_RESISTANCE;
        checkDurationBounds();

        potionEffectType = PotionEffectType.FIRE_RESISTANCE;
        informousText = legilimensText = "does not fear fire";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}
