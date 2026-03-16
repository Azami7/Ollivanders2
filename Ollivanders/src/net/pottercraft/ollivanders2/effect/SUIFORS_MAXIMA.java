package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Transforms the target player into a pig.
 *
 * @author Azami7
 * @see PlayerTransformBase
 */
public class SUIFORS_MAXIMA extends PlayerTransformBase {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public SUIFORS_MAXIMA(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SUIFORS_MAXIMA;
        entityType = EntityType.PIG;

        checkDurationBounds();
    }
}
