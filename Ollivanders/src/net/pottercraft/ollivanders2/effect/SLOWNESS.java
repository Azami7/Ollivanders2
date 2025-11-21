package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Movement slowness potion effect that reduces the affected player's movement speed.
 *
 * <p>SLOWNESS applies Minecraft's SLOWNESS potion effect to decrease the target player's movement
 * and attack speed. The effect reduces how quickly the player can move, making them sluggish and
 * slowing their combat capabilities. The effect is powered by the Minecraft potion effect system
 * with strength (amplifier) set to 1. The effect is detectable by both mind-reading spells
 * (Legilimens) and information spells (Informous) which report the target "feels sluggish". The
 * player receives a notification of "You feel sluggish." when the effect is applied.</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class SLOWNESS extends PotionEffectSuper {
    /**
     * Constructor for creating a movement slowness effect.
     *
     * <p>Creates a potion effect that reduces the target player's movement and attack speed using
     * Minecraft's SLOWNESS potion effect type with strength 1. Sets detection text for both
     * mind-reading spells (Legilimens) and information spells (Informous) to "feels sluggish", and
     * notifies the player "You feel sluggish." when the effect is applied.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the slowness effect in game ticks
     * @param pid      the unique ID of the player to slow
     */
    public SLOWNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.SLOWNESS;
        potionEffectType = PotionEffectType.SLOWNESS;
        informousText = legilimensText = "feels sluggish";
        affectedPlayerText = "You feel sluggish.";
    }

    /**
     * Perform cleanup when the slowness effect is removed.
     *
     * <p>The default implementation does nothing, as SLOWNESS is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's movement speed returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}