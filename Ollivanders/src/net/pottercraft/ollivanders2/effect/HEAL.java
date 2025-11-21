package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Instant healing potion effect that restores the affected player's health.
 *
 * <p>HEAL applies Minecraft's INSTANT_HEALTH potion effect to restore health to the
 * target player. Unlike effects that restore health over time, HEAL delivers its healing
 * instantly when applied. The effect is powered by the Minecraft potion effect system
 * with strength (amplifier) set to 1, determining the healing magnitude. The effect is
 * detectable by mind-reading spells (Legilimens) which report the target "feels healthy".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 * @see HARM for the instant damage counterpart to this healing effect
 */
public class HEAL extends PotionEffectSuper {
    /**
     * Constructor for creating an instant healing potion effect.
     *
     * <p>Creates a potion effect that restores health to the target player using
     * Minecraft's INSTANT_HEALTH potion effect type. The effect is detected by
     * information spells as the target "feels healthy".</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect in game ticks (unused for instant healing)
     * @param pid      the unique ID of the player to heal
     */
    public HEAL(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.HEAL;
        potionEffectType = PotionEffectType.INSTANT_HEALTH;
        informousText = legilimensText = "feels healthy";
    }

    /**
     * Perform cleanup when the instant healing effect is removed.
     *
     * <p>The default implementation does nothing, as INSTANT_HEALTH is an instant potion
     * effect with no persistent state to clean up. The healing is applied immediately when
     * the effect is created, so no additional cleanup is required on removal.</p>
     */
    @Override
    public void doRemove() {
    }
}