package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's NAUSEA potion effect to the target to simulate magical confusion. Detectable via Informous and
 * Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class CONFUSION extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to confuse
     */
    public CONFUSION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.CONFUSION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.NAUSEA;
        informousText = legilimensText = "feels confused";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}