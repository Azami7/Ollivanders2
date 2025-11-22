package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Enhanced movement speed potion effect that significantly increases the affected player's movement speed.
 *
 * <p>SPEED_SPEEDIER is an enhanced variant of the SPEED effect that applies Minecraft's SPEED potion
 * effect with increased potency (strength 2 instead of 1). This effect produces a much more pronounced
 * speed boost, allowing the player to move at significantly faster velocities than the base SPEED effect.
 * The enhanced movement speed improves traversal capabilities and combat mobility. The effect is detectable
 * by both mind-reading spells (Legilimens) and information spells (Informous) which report the target
 * "is moving very fast".</p>
 *
 * @author Azami7
 * @see SPEED for the standard speed enhancement effect
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class SPEED_SPEEDIER extends PotionEffectSuper {
    /**
     * Constructor for creating an enhanced movement speed effect.
     *
     * <p>Creates an enhanced potion effect that significantly increases the target player's movement speed
     * using Minecraft's SPEED potion effect type with strength 2 (double the potency of the standard SPEED
     * effect). Sets detection text for both mind-reading spells (Legilimens) and information spells (Informous)
     * to "is moving very fast".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to enhance with increased speed
     */
    public SPEED_SPEEDIER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 2;

        effectType = O2EffectType.SPEED_SPEEDIER;
        potionEffectType = PotionEffectType.SPEED;
        informousText = legilimensText = "is moving very fast";
    }

    /**
     * Perform cleanup when the enhanced speed effect is removed.
     *
     * <p>The default implementation does nothing, as SPEED_SPEEDIER is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is manually
     * removed, the player's movement speed returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}
