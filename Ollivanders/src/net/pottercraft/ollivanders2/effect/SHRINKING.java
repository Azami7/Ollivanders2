package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Causes a player to shrink in scale.
 *
 * @since 2.21
 * @author Azami7
 */
public class SHRINKING extends PlayerChangeSizeSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public SHRINKING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.SHRINKING;
        scaleMultiplier = 0.5; // makes the player half size

        informousText = "is unnaturally small";

        startEffect();
    }
}
