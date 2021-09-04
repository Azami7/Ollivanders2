package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Broom flying effect - different in that it is always permanent and doesn't make a smoke effect.
 *
 * @author Azami7
 */
public class BROOM_FLYING extends FLYING
{
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public BROOM_FLYING (@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
    {
        super(plugin, duration, pid);

        effectType = O2EffectType.FLYING;

        doSmokeEffect = false;
        permanent = true;
    }
}
