package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's SLOWNESS potion effect (amplifier 1) to reduce the target's movement and attack speed.
 * Detectable via Informous and Legilimens.
 *
 * @author Azami7
 */
public class SLOWNESS extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to slow
     */
    public SLOWNESS(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.SLOWNESS;
        checkDurationBounds();

        potionEffectType = PotionEffectType.SLOWNESS;

        informousText = legilimensText = "feels sluggish";
        affectedPlayerText = "You feel sluggish.";
    }

    /**
     * No cleanup needed; the Minecraft potion effect expires on its own.
     */
    @Override
    public void doRemove() {
    }
}