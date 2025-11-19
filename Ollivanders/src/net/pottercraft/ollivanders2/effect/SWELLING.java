package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Causes a player to increase in scale.
 *
 * @since 2.21
 * @author Azami7
 */
public class SWELLING extends PlayerChangeSizeSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public SWELLING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.SWELLING;
        scaleMultiplier = 2; // increase the player's by double

        informousText = "is unnaturally large";

        startEffect();
    }
}
