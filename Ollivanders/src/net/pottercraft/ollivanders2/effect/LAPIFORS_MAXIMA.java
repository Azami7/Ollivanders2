package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Transforms the target player into a rabbit.
 *
 * @author Azami7
 * @see PlayerTransformBase
 */
public class LAPIFORS_MAXIMA extends PlayerTransformBase {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public LAPIFORS_MAXIMA(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.LAPIFORS_MAXIMA;
        entityType = EntityType.RABBIT;

        checkDurationBounds();
    }

    /**
     * Sets a random rabbit style
     */
    void setAnimalVariant() {
        if (spawnedEntity != null) {
            ((Rabbit) spawnedEntity).setRabbitType(EntityCommon.getRandomRabbitType());
        }
    }
}
