package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Doubles the affected player's scale (and hitbox). Detectable via Informous.
 *
 * @author Azami7
 * @see PlayerChangeSize
 */
public class SWELLING extends PlayerChangeSize {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the swelling effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to enlarge
     */
    public SWELLING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SWELLING;
        checkDurationBounds();

        scaleMultiplier = 2;

        informousText = "is unnaturally large";
    }
}
