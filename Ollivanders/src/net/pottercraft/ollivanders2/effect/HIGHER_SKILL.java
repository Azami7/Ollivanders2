package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker that boosts the player's effective skill level: the effect itself does nothing each tick; the skill
 * system checks for its presence and applies the boost. Detectable via Legilimens.
 *
 * @author Azami7
 * @see FAST_LEARNING for a similar passive boost to skill experience gain
 */
public class HIGHER_SKILL extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent true if this effect is permanent, false if it is not
     * @param pid         the unique ID of the player to grant higher skill
     */
    public HIGHER_SKILL(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.HIGHER_SKILL;
        checkDurationBounds();

        legilimensText = "feels more skillful than usual";
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    @Override
    public void doRemove() {
    }
}