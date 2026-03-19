package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.O2Color;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Transforms the target player into a dog.
 *
 * @author Azami7
 * @see PlayerTransformBase
 */
public class CANIFORS_MAXIMA extends PlayerTransformBase {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public CANIFORS_MAXIMA(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.CANIFORS_MAXIMA;
        entityType = EntityType.WOLF;

        checkDurationBounds();
    }

    /**
     * Makes the wolf a dog form and sets a random collar color.
     */
    void setAnimalVariant() {
        if (spawnedEntity != null) {
            ((Wolf) spawnedEntity).setTamed(true);
            ((Wolf) spawnedEntity).setCollarColor(O2Color.getRandomPrimaryDyeableColor().getDyeColor());
        }
    }
}
