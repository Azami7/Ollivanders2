package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Instant damage potion effect that harms the affected player.
 *
 * <p>HARM applies Minecraft's INSTANT_DAMAGE potion effect to inflict sudden damage
 * to the target player. Unlike the BURNING effect which inflicts periodic damage over
 * time, HARM delivers its damage instantly when applied. The effect is powered by the
 * Minecraft potion effect system with strength (amplifier) set to 1, determining the
 * damage magnitude. The effect is detectable by mind-reading spells (Legilimens) which
 * report the target "feels unwell".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 * @see BURNING for a time-based damage alternative
 */
public class HARM extends PotionEffectSuper {
    /**
     * Constructor for creating an instant damage potion effect.
     *
     * <p>Creates a potion effect that applies instant damage to the target player using
     * Minecraft's INSTANT_DAMAGE potion effect type. The effect is detected by
     * information spells as the target "feels unwell".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to harm
     */
    public HARM(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.HARM;
        checkDurationBounds();

        informousText = legilimensText = "feels unwell";

        potionEffectType = PotionEffectType.INSTANT_DAMAGE;
        strength = 1;
    }

    /**
     * Perform cleanup when the instant damage effect is removed.
     *
     * <p>The default implementation does nothing, as INSTANT_DAMAGE is an instant potion
     * effect with no persistent state to clean up. The damage is applied immediately when
     * the effect is created, so no additional cleanup is required on removal.</p>
     */
    @Override
    public void doRemove() {
    }
}