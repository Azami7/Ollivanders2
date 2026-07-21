package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's WEAKNESS potion effect to reduce the target's melee attack damage. Detectable via Informous and
 * Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class WEAKNESS extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to weaken
     */
    public WEAKNESS(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.WEAKNESS;
        checkDurationBounds();

        potionEffectType = PotionEffectType.WEAKNESS;
        strength = 1;

        informousText = legilimensText = "feels weak";
        affectedPlayerText = "You feel weak.";
    }

    @Override
    public void doRemove() {
    }
}