package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Water breathing potion effect that allows the affected player to breathe underwater.
 *
 * <p>WATER_BREATHING applies Minecraft's WATER_BREATHING potion effect to allow the target player
 * to breathe underwater without consuming their oxygen meter. The effect enables underwater respiration,
 * allowing the player to explore aquatic environments indefinitely without taking drowning damage.
 * The effect is powered by the Minecraft potion effect system with strength (amplifier) set to 1.
 * The effect is detectable by both mind-reading spells (Legilimens) and information spells (Informous)
 * which report the target "can breathe in water".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class WATER_BREATHING extends PotionEffectSuper {
    /**
     * Constructor for creating a water breathing effect.
     *
     * <p>Creates a potion effect that allows the target player to breathe underwater using Minecraft's
     * WATER_BREATHING potion effect type with strength 1. Sets detection text for both mind-reading
     * spells (Legilimens) and information spells (Informous) to "can breathe in water".</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the water breathing effect in game ticks
     * @param pid      the unique ID of the player to grant water breathing
     */
    public WATER_BREATHING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.WATER_BREATHING;
        potionEffectType = PotionEffectType.WATER_BREATHING;
        informousText = legilimensText = "can breath in water";
    }

    /**
     * Perform cleanup when the water breathing effect is removed.
     *
     * <p>The default implementation does nothing, as WATER_BREATHING is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is manually
     * removed, the player's normal underwater respiration behavior returns.</p>
     */
    @Override
    public void doRemove() {
    }
}
