package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Parent class for antidotes that neutralize a standard Minecraft potion effect.
 *
 * <p>Instant effect: on its first tick it removes the target potion effect and, if {@link #strength} is below 1.0,
 * re-applies it with a proportionally reduced duration, then kills itself. Subclasses set {@link #potionEffectType}
 * to choose which potion effect to counter.</p>
 *
 * @author Azami7
 */
public abstract class PotionEffectAntidote extends O2Effect {
    /**
     * The Minecraft potion effect this antidote counters. Subclasses set this to POISON, HARM, WEAKNESS, etc.
     */
    PotionEffectType potionEffectType = PotionEffectType.GLOWING;

    /**
     * Duration multiplier in [0.0, 1.0] controlling potency: the countered effect is re-applied with
     * {@code originalDuration * strength}. A value of 1.0 removes it entirely.
     */
    double strength = 0.25;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - antidotes apply immediately and expire
     * @param isPermanent ignored - antidotes apply immediately and expire
     * @param pid         the unique ID of the target player to receive this antidote
     */
    public PotionEffectAntidote(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, false, pid);
    }

    /**
     * Remove the countered potion effect from the target and, if {@link #strength} is below 1.0, re-apply it with
     * {@code originalDuration * strength}, then kill this antidote. No-op if the target does not have the effect.
     */
    @Override
    public void checkEffect() {
        PotionEffect targetEffect = target.getPotionEffect(potionEffectType);

        if (targetEffect != null) {
            target.removePotionEffect(potionEffectType);

            if (strength < 1) {
                int newDuration = (int) (targetEffect.getDuration() * strength);

                target.addPotionEffect(new PotionEffect(potionEffectType, newDuration, targetEffect.getAmplifier()));
            }
        }

        kill();
    }

    /**
     * Get this antidote's strength: the duration multiplier applied to the countered effect. A value of 1.0 or higher
     * removes the effect entirely.
     *
     * @return the antidote strength
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Potion antidote effects cannot ever be permanent
     *
     * @param perm ignored - potion effects are always temporary
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
