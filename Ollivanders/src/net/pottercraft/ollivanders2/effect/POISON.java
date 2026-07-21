package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's POISON potion effect to inflict periodic damage on the target; the poison damage stops before
 * killing them. Detectable via Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class POISON extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to poison
     */
    public POISON(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.POISON;
        checkDurationBounds();

        potionEffectType = PotionEffectType.POISON;
        informousText = legilimensText = "feels sick";
    }

    @Override
    public void doRemove() {
    }
}