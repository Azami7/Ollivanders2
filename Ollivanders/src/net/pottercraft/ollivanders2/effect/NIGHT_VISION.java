package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Night vision potion effect that allows the player to see clearly in darkness.
 *
 * <p>NIGHT_VISION applies Minecraft's NIGHT_VISION potion effect to enhance the target player's
 * vision in dark environments. The effect allows the player to see clearly as if it were daytime,
 * even in complete darkness or underground. The effect is powered by the Minecraft potion effect
 * system with strength (amplifier) set to 1. The effect is detectable by both mind-reading spells
 * (Legilimens) and information spells (Informous) which report the target "can see in darkness".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class NIGHT_VISION extends PotionEffectSuper {
    /**
     * Constructor for creating a night vision potion effect.
     *
     * <p>Creates a potion effect that enhances the target player's vision in darkness using
     * Minecraft's NIGHT_VISION potion effect type. Sets detection text for both mind-reading spells
     * (Legilimens) and information spells (Informous) to "can see in darkness".</p>
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

    /**
     * Perform cleanup when the night vision effect is removed.
     *
     * <p>The default implementation does nothing, as NIGHT_VISION is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's vision returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}
