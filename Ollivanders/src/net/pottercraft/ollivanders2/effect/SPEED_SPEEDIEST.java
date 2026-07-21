package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's SPEED potion effect at strength 4, the maximum-potency variant of the {@link SPEED} family
 * (above {@link SPEED_SPEEDIER}). Detectable via Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class SPEED_SPEEDIEST extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to enhance with extreme speed
     */
    public SPEED_SPEEDIEST(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 4;

        effectType = O2EffectType.SPEED_SPEEDIEST;
        checkDurationBounds();

        potionEffectType = PotionEffectType.SPEED;

        informousText = legilimensText = "is moving extremely fast";
    }

    @Override
    public void doRemove() {
    }
}