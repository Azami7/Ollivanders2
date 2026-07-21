package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's SPEED potion effect at strength 2 — a stronger variant of {@link SPEED}. Detectable via Informous
 * and Legilimens.
 *
 * @author Azami7
 * @see SPEED
 * @see PotionEffect
 */
public class SPEED_SPEEDIER extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to enhance with increased speed
     */
    public SPEED_SPEEDIER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 2;

        effectType = O2EffectType.SPEED_SPEEDIER;
        checkDurationBounds();

        potionEffectType = PotionEffectType.SPEED;

        informousText = legilimensText = "is moving very fast";
    }

    @Override
    public void doRemove() {
    }
}
