package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Instantly restores the target's health via Minecraft's INSTANT_HEALTH potion effect. Detectable via Informous and
 * Legilimens ("feels healthy").
 *
 * @author Azami7
 * @see PotionEffect
 * @see HARM
 */
public class HEAL extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to heal
     */
    public HEAL(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.HEAL;
        checkDurationBounds();

        potionEffectType = PotionEffectType.INSTANT_HEALTH;
        informousText = legilimensText = "feels healthy";
    }

    @Override
    public void doRemove() {
    }
}