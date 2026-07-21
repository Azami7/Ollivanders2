package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Shrinks the affected player to half their normal scale (scaleMultiplier 0.5). Detectable via Informous.
 *
 * @author Azami7
 * @see PlayerChangeSize
 */
public class SHRINKING extends PlayerChangeSize {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shrinking effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to shrink
     */
    public SHRINKING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SHRINKING;
        checkDurationBounds();

        informousText = "is unnaturally small";

        scaleMultiplier = 0.5; // makes the player half size
    }
}
