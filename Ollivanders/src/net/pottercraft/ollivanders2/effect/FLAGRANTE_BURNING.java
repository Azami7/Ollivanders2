package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Permanent burning inflicted by a flagrante-cursed item. A {@link BURNING} variant that is always permanent and cannot
 * be made temporary via {@link #setPermanent(boolean)}.
 *
 * @author Azami7
 * @see BURNING
 */
public class FLAGRANTE_BURNING extends BURNING {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - flagrante burning is always permanent
     * @param isPermanent ignored - flagrante burning is always permanent
     * @param pid         the unique ID of the player affected by the cursed item
     */
    public FLAGRANTE_BURNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.FLAGRANTE_BURNING;
        checkDurationBounds();
    }

    /**
     * No-op: flagrante burning is always permanent and cannot be made temporary.
     *
     * @param perm ignored - flagrante burning is always permanent
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
