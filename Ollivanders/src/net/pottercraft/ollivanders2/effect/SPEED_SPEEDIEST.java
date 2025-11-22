package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Maximum movement speed potion effect that grants the affected player extreme movement speed.
 *
 * <p>SPEED_SPEEDIEST is the maximum potency variant of the SPEED effect family, applying Minecraft's
 * SPEED potion effect with the highest available strength (strength 4). This effect produces the most
 * extreme speed boost, allowing the player to move at dramatically increased velocities far beyond the
 * base SPEED and intermediate SPEED_SPEEDIER effects. The maximum movement speed is optimal for rapid
 * traversal and extreme combat mobility scenarios. The effect is detectable by both mind-reading spells
 * (Legilimens) and information spells (Informous) which report the target "is moving extremely fast".</p>
 *
 * @author Azami7
 * @see SPEED for the standard speed enhancement effect
 * @see SPEED_SPEEDIER for the intermediate enhanced speed effect
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class SPEED_SPEEDIEST extends PotionEffectSuper {
    /**
     * Constructor for creating a maximum movement speed effect.
     *
     * <p>Creates the maximum potency potion effect that grants the target player extreme movement speed
     * using Minecraft's SPEED potion effect type with strength 4 (four times the potency of the standard
     * SPEED effect). Sets detection text for both mind-reading spells (Legilimens) and information spells
     * (Informous) to "is moving extremely fast".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to enhance with extreme speed
     */
    public SPEED_SPEEDIEST(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 4;

        effectType = O2EffectType.SPEED_SPEEDIEST;
        potionEffectType = PotionEffectType.SPEED;
        informousText = legilimensText = "is moving extremely fast";
    }

    /**
     * Perform cleanup when the maximum speed effect is removed.
     *
     * <p>The default implementation does nothing, as SPEED_SPEEDIEST is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is manually
     * removed, the player's movement speed returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}