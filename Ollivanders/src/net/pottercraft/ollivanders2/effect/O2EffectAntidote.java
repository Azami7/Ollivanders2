package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Abstract base class for antidote effects that counteract another active effect on the target.
 * <p>
 * On its first tick an antidote reduces or removes the effect named by {@link #o2EffectType}, scaled by
 * {@link #strength}: a strength &ge; 1.0 removes it entirely, while a strength &lt; 1.0 reduces its remaining
 * duration by that fraction. It then kills itself. If the target does not have that effect, it expires with no
 * action.
 * </p>
 *
 * @author Azami7
 * @see O2EffectType
 */
public abstract class O2EffectAntidote extends O2Effect {
    /**
     * The effect type this antidote counteracts; subclasses set this to the effect they neutralize.
     */
    O2EffectType o2EffectType = O2EffectType.BABBLING;

    /**
     * The potency on a 0.0-to-1.0+ scale: &ge; 1.0 fully removes the target effect, &lt; 1.0 reduces its remaining
     * duration by that fraction (0.5 = 50%). Defaults to 0.25.
     */
    double strength = 0.25;

    /**
     * Constructor. The duration and isPermanent arguments are ignored: the antidote is forced permanent so it is
     * not aged away before {@link #checkEffect()} runs, then it kills itself on the first tick.
     *
     * @param plugin      a reference to the plugin
     * @param duration    ignored
     * @param isPermanent ignored
     * @param pid         the unique ID of the target player
     */
    public O2EffectAntidote(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);
    }

    /**
     * Apply the antidote to {@link #o2EffectType} on the target per {@link #strength}, then kill this effect. No-op
     * against the target effect if the target does not have it.
     */
    @Override
    public void checkEffect() {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(targetID, o2EffectType)) {
            if (strength < 1) {
                int percent = (int) (strength * 100);
                Ollivanders2API.getPlayers().playerEffects.ageEffectByPercent(targetID, o2EffectType, percent);
            }
            else {
                Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, o2EffectType);
            }
        }

        kill();
    }

    /**
     * Get the potency of this antidote.
     *
     * @return the strength; &ge; 1.0 fully removes the target effect, &lt; 1.0 reduces its duration by that fraction
     */
    public double getStrength() {
        return strength;
    }

    /**
     * No-op: antidotes are always permanent during their single-tick execution, so requests to change the flag are
     * ignored.
     *
     * @param perm ignored
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
