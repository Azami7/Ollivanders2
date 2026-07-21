package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker that boosts the target's skill experience gain. The effect has no active behavior; the skill system
 * applies an experience multiplier while this marker is present. Detectable via Legilimens.
 *
 * @author Azami7
 */
public class FAST_LEARNING extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent true if this effect is permanent, false if it is not
     * @param pid         the unique ID of the player to grant fast learning
     */
    public FAST_LEARNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FAST_LEARNING;
        checkDurationBounds();

        legilimensText = "feels more mentally alert than usual";
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    @Override
    public void doRemove() {
    }
}