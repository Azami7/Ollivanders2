package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's blindness potion effect to obscure the player's vision. Detectable via Informous and
 * Legilimens as "cannot see".
 *
 * @author Azami7
 * @see PotionEffect
 */
public class BLINDNESS extends PotionEffect {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to blind
     */
    public BLINDNESS(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.BLINDNESS;
        checkDurationBounds();

        potionEffectType = PotionEffectType.BLINDNESS;
        informousText = legilimensText = "cannot see";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}