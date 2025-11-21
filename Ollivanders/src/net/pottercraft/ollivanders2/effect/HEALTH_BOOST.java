package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Sustained health boost potion effect that temporarily increases max health.
 *
 * <p>HEALTH_BOOST applies Minecraft's HEALTH_BOOST potion effect to temporarily increase
 * the target player's maximum health capacity for the duration of the effect. Unlike the
 * HEAL instant effect which restores health immediately, HEALTH_BOOST maintains an elevated
 * maximum health cap while the effect is active. The effect is powered by the Minecraft
 * potion effect system with strength (amplifier) set to 1, determining the additional health
 * capacity. When the effect expires, the player's maximum health returns to normal. The effect
 * is detectable by mind-reading spells (Legilimens) which report the target "feels stronger".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 * @see HEAL for the instant healing effect alternative
 */
public class HEALTH_BOOST extends PotionEffectSuper {
    /**
     * Constructor for creating a sustained health boost potion effect.
     *
     * <p>Creates a potion effect that increases the target player's maximum health using
     * Minecraft's HEALTH_BOOST potion effect type. The effect is detected by information
     * spells as the target "feels stronger".</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration in ticks, snapped to min of 2 minutes, max of 10 minutes
     * @param pid      the unique ID of the player to boost
     */
    public HEALTH_BOOST(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.HEALTH_BOOST;
        potionEffectType = PotionEffectType.HEALTH_BOOST;
        informousText = legilimensText = "feels stronger";
    }

    /**
     * Perform cleanup when the health boost effect is removed.
     *
     * <p>The default implementation does nothing, as HEALTH_BOOST is a potion effect whose
     * effects are automatically managed by the Minecraft potion system. When the effect expires
     * or is manually removed, the player's maximum health automatically returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}