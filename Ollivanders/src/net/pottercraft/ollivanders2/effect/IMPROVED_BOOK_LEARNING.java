package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker effect whose presence causes the skill system to boost the player's experience gain from reading
 * books; it applies no changes itself. Detectable via Legilimens.
 *
 * @author Azami7
 * @see FAST_LEARNING
 * @see HIGHER_SKILL
 */
public class IMPROVED_BOOK_LEARNING extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent true if this effect is permanent, false if it is not
     * @param pid         the unique ID of the player to grant improved book learning
     */
    public IMPROVED_BOOK_LEARNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.IMPROVED_BOOK_LEARNING;
        checkDurationBounds();

        legilimensText = "feels more studious than usual";
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    @Override
    public void doRemove() {
    }
}