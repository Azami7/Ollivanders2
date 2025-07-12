package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Increases the experience a player gets for using a skill.
 *
 * @author Azami7
 */
public class FAST_LEARNING extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public FAST_LEARNING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.FAST_LEARNING;
        legilimensText = "feels more mentally alert than usual";
    }

    /**
     * Age this effect each game tick.
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}