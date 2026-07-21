package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's night vision potion effect so the player can see clearly in darkness. Detectable via Informous
 * and Legilimens as "can see in darkness".
 *
 * @author Azami7
 * @see PotionEffect
 */
public class NIGHT_VISION extends PotionEffect {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to grant night vision
     */
    public NIGHT_VISION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.NIGHT_VISION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.NIGHT_VISION;
        informousText = legilimensText = "can see in darkness";
    }

    @Override
    public void doRemove() {
    }
}
