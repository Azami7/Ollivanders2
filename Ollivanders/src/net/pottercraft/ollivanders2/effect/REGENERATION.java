package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's REGENERATION potion effect at strength 1, restoring the target's health over time. Detectable
 * via Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class REGENERATION extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin the plugin instance
     * @param duration the duration of the effect in server ticks
     * @param isPermanent whether the effect should be permanent
     * @param pid the UUID of the player affected by this effect
     */
    public REGENERATION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.REGENERATION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.REGENERATION;
        informousText = legilimensText = "feels healthy";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}
