package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Effect type that is an antidote to O2Effects
 */
public abstract class O2EffectAntidoteSuper extends O2Effect {
    /**
     * the o2effect type this effect is the antidote for
     */
    O2EffectType o2EffectType = O2EffectType.BABBLING;

    /**
     * strength of this antidote - strength of 1 will cancel the potion effect entirely, 0.5 reduced by half-time, 0.25 reduced by quarter time, etc.
     */
    double strength = 0.25;

    /**
     * Constructor
     *
     * @param plugin   a reference to the plugin for logging
     * @param duration how much time does this antidote cut from the effect's duration
     * @param pid      the target player
     */
    public O2EffectAntidoteSuper(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        // hard-code duration because these are instant effects
        super(plugin, duration, pid);
    }

    /**
     * Do the effect immediately on the target and kill.
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
}
