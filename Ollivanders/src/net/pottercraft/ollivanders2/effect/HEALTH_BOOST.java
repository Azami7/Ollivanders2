package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Temporarily raises the target's maximum health via Minecraft's HEALTH_BOOST potion effect. Detectable via Informous
 * and Legilimens ("feels stronger").
 *
 * @author Azami7
 * @see PotionEffect
 * @see HEAL
 */
public class HEALTH_BOOST extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to boost
     */
    public HEALTH_BOOST(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.HEALTH_BOOST;
        checkDurationBounds();

        potionEffectType = PotionEffectType.HEALTH_BOOST;
        informousText = legilimensText = "feels stronger";
    }

    @Override
    public void doRemove() {
    }
}