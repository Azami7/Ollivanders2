package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's SATURATION potion effect (amplifier 1), keeping the player's hunger bar full. Detectable via
 * Informous and Legilimens.
 *
 * @author Azami7
 */
public class SATIATION extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin the plugin instance
     * @param duration the duration of the effect in server ticks
     * @param isPermanent whether the effect should be permanent
     * @param pid the UUID of the player affected by this effect
     */
    public SATIATION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SATIATION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.SATURATION;
        informousText = legilimensText = "feels full";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}
