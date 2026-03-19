package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Transforms the target player into a polar bear, with a 5% chance of becoming a panda instead.
 *
 * @author Azami7
 * @see PlayerTransformBase
 */
public class URSIFORS_MAXIMA extends PlayerTransformBase {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public URSIFORS_MAXIMA(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.URSIFORS_MAXIMA;
        entityType = EntityType.POLAR_BEAR;

        checkDurationBounds();
    }

    /**
     * Randomly select polar bear (95%) or panda (5%) and spawn it.
     */
    @Override
    void spawnEntity() {
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 20);

        if (rand < 1) // 5% chance
            entityType = EntityType.PANDA;
        else
            entityType = EntityType.POLAR_BEAR;

        super.spawnEntity();
    }
}
