package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Weakness potion effect that reduces the affected player's melee attack damage.
 *
 * <p>WEAKNESS applies Minecraft's WEAKNESS potion effect to decrease the target player's physical
 * attack damage. The effect reduces the damage dealt by melee attacks, making the affected player
 * less effective in combat. The effect is powered by the Minecraft potion effect system with
 * strength (amplifier) set to 1. The effect is detectable by both mind-reading spells (Legilimens)
 * and information spells (Informous) which report the target "feels weak". The player receives a
 * notification of "You feel weak." when the effect is applied.</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class WEAKNESS extends PotionEffectSuper {
    /**
     * Constructor for creating a weakness effect.
     *
     * <p>Creates a potion effect that reduces the target player's melee attack damage using Minecraft's
     * WEAKNESS potion effect type with strength 1. Sets detection text for both mind-reading spells
     * (Legilimens) and information spells (Informous) to "feels weak", and notifies the player
     * "You feel weak." when the effect is applied.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to weaken
     */
    public WEAKNESS(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.WEAKNESS;
        checkDurationBounds();

        potionEffectType = PotionEffectType.WEAKNESS;
        strength = 1;

        informousText = legilimensText = "feels weak";
        affectedPlayerText = "You feel weak.";
    }

    /**
     * Perform cleanup when the weakness effect is removed.
     *
     * <p>The default implementation does nothing, as WEAKNESS is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's attack damage returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}