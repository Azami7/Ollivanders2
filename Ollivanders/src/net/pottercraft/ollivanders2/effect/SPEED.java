package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Movement speed enhancement potion effect that increases the affected player's movement speed.
 *
 * <p>SPEED applies Minecraft's SPEED potion effect to increase the target player's movement
 * velocity. The effect enhances how quickly the player can move, improving their traversal speed
 * and mobility in combat scenarios. The effect is powered by the Minecraft potion effect system
 * with strength (amplifier) set to 1. The effect is detectable by both mind-reading spells
 * (Legilimens) and information spells (Informous) which report the target "is moving fast".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class SPEED extends PotionEffectSuper {
    /**
     * Constructor for creating a movement speed enhancement effect.
     *
     * <p>Creates a potion effect that increases the target player's movement speed using Minecraft's
     * SPEED potion effect type with strength 1. Sets detection text for both mind-reading spells
     * (Legilimens) and information spells (Informous) to "is moving fast".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to enhance with speed
     */
    public SPEED(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.SPEED;
        potionEffectType = PotionEffectType.SPEED;
        informousText = legilimensText = "is moving fast";
    }

    /**
     * Perform cleanup when the speed effect is removed.
     *
     * <p>The default implementation does nothing, as SPEED is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's movement speed returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}