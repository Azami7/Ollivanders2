package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.O2Color;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Transforms the target player into a cat.
 *
 * @author Azami7
 * @see PlayerTransformBase
 */
public class FELIFORS_MAXIMA extends PlayerTransformBase {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public FELIFORS_MAXIMA(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FELIFORS_MAXIMA;
        entityType = EntityType.CAT;

        checkDurationBounds();
    }

    /**
     * Sets a random cat pattern.
     */
    void setAnimalVariant() {
        if (spawnedEntity != null) {
            ((Cat) spawnedEntity).setCatType(EntityCommon.getRandomCatType());
            ((Cat) spawnedEntity).setCollarColor(O2Color.getRandomDyeableColor().getDyeColor());
        }
    }
}
