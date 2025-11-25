package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Abstract base class for antidote effects that counteract other magical effects.
 *
 * <p>An antidote is a special type of effect that, when applied, immediately reduces or completely cancels
 * another active effect on the target player. Antidotes work on a strength scale (0.0 to 1.0+):</p>
 * <ul>
 * <li>strength = 1.0: Completely removes the target effect</li>
 * <li>strength = 0.5: Reduces the target effect's duration by 50%</li>
 * <li>strength = 0.25: Reduces the target effect's duration by 25%</li>
 * <li>strength > 1.0: Completely removes the target effect (same as 1.0)</li>
 * </ul>
 *
 * <p>Antidotes execute their effect immediately in checkEffect() on the first tick, then kill themselves.
 * If the target player does not have the effect being countered, the antidote simply expires without doing anything.</p>
 *
 * @author Azami7
 * @see O2EffectType for available effect types that can be countered
 */
public abstract class O2EffectAntidoteSuper extends O2Effect {
    /**
     * The O2EffectType that this antidote counteracts.
     * Subclasses should override this to specify which effect type they neutralize.
     */
    O2EffectType o2EffectType = O2EffectType.BABBLING;

    /**
     * The potency of this antidote on a scale from 0.0 to 1.0+.
     * Values >= 1.0 completely remove the target effect.
     * Values < 1.0 reduce the target effect's duration by that percentage (e.g., 0.5 = 50% reduction).
     * Default is 0.25 (25% reduction).
     */
    double strength = 0.25;

    /**
     * Constructor for creating an antidote effect.
     *
     * <p>Creates an antidote that will immediately neutralize a target effect on the first tick.
     * The duration parameter is provided for API consistency but antidotes are instant-acting effects
     * that kill themselves immediately after application.</p>
     *
     * @param plugin      a reference to the plugin for API access and logging
     * @param duration    ignored - antidotes apply immediately and do not persist
     * @param isPermanent ignored - antidotes are immediately applied and resolved
     * @param pid         the unique ID of the target player who will receive the antidote
     */
    public O2EffectAntidoteSuper(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        // set isPermanent to true so we do not need to worry about what duration was set to, checkEffect() will kill it
        super(plugin, duration, true, pid);
    }

    /**
     * Apply the antidote effect and immediately kill this effect.
     *
     * <p>Checks if the target player has the effect specified in o2EffectType active.
     * If found, applies the antidote based on strength:
     * <ul>
     * <li>If strength &ge; 1.0: Completely removes the target effect</li>
     * <li>If strength &lt; 1.0: Reduces the target effect's remaining duration by the percentage specified</li>
     * </ul>
     * <p>
     * If the target effect is not found on the player, does nothing.
     * In either case, this antidote effect immediately kills itself to prevent re-execution.</p>
     */
    @Override
    public void checkEffect() {
        // Check if the target player has the effect this antidote counteracts
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(targetID, o2EffectType)) {
            if (strength < 1) {
                // Partial antidote: reduce the target effect's duration by the strength percentage
                // Example: strength = 0.5 means reduce by 50%, strength = 0.25 means reduce by 25%
                int percent = (int) (strength * 100);
                Ollivanders2API.getPlayers().playerEffects.ageEffectByPercent(targetID, o2EffectType, percent);
            }
            else {
                // Full antidote (strength >= 1.0): completely remove the target effect
                Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, o2EffectType);
            }
        }

        // Antidotes are instant effects - kill immediately to prevent re-execution on next tick
        kill();
    }

    /**
     * Get the strength/potency of this antidote effect.
     *
     * <p>Returns a value between 0.0 and 1.0+ that determines how effectively this antidote
     * counteracts the target effect:</p>
     * <ul>
     * <li><strong>strength &ge; 1.0:</strong> Full-strength antidote - completely removes the target effect</li>
     * <li><strong>0.5 &le; strength &lt; 1.0:</strong> Medium-strength antidote - reduces target effect duration by 50-99%</li>
     * <li><strong>0.0 &lt; strength &lt; 0.5:</strong> Weak antidote - reduces target effect duration by less than 50%</li>
     * </ul>
     *
     * @return the antidote strength as a decimal value (0.0 to 1.0+)
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Overridden to prevent changing permanent status - antidotes are always permanent during execution.
     *
     * <p>Antidotes are designed as instant-acting effects that apply their counter-effect immediately
     * and then kill themselves on the first tick. The permanent flag is set internally to ensure the
     * effect is not aged or expired prematurely before checkEffect() has a chance to execute.
     * Attempts to change the permanent status are silently ignored.</p>
     *
     * @param perm the requested permanent status (ignored - antidotes are always internally set as permanent)
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
