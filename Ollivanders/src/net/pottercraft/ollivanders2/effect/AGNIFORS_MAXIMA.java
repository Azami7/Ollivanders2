package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Transforms the target player into a sheep.
 *
 * @author Azami7
 * @see PlayerTransformBase
 */
public class AGNIFORS_MAXIMA extends PlayerTransformBase {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public AGNIFORS_MAXIMA(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.AGNIFORS_MAXIMA;
        entityType = EntityType.SHEEP;

        checkDurationBounds();
    }

    /**
     * Sets a random natural sheep color.
     */
    void setAnimalVariant() {
        if (spawnedEntity != null) {
            ((Sheep) spawnedEntity).setColor(EntityCommon.getRandomNaturalSheepColor());
        }
    }
}
