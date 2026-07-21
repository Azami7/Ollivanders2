package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * {@link FLYING} variant for broom flight: always permanent (duration is ignored) and with smoke particles disabled.
 *
 * @author Azami7
 * @see FLYING for the base flight effect mechanism
 */
public class BROOM_FLYING extends FLYING {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - broom flight is always permanent
     * @param isPermanent ignored - broom flight is always permanent
     * @param pid         the unique ID of the player to give broom flight
     */
    public BROOM_FLYING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.BROOM_FLYING;
        checkDurationBounds();

        doSmokeEffect = false;
    }

    @Override
    public void setPermanent(boolean perm) {
    }
}
