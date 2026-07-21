package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Grants the target Minecraft's WATER_BREATHING potion effect (amplifier 1) so they can breathe underwater.
 * Detectable via Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class WATER_BREATHING extends PotionEffect {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, clamped to the effect type's 2-to-5-minute bounds
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to grant water breathing
     */
    public WATER_BREATHING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.WATER_BREATHING;
        checkDurationBounds();

        strength = 1;
        potionEffectType = PotionEffectType.WATER_BREATHING;

        informousText = legilimensText = "can breath in water";
    }

    @Override
    public void doRemove() {
    }
}
