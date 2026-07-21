package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Temporary marker that the player is currently reciting the Animagus incantation, distinct from the permanent
 * {@link ANIMAGUS_EFFECT} that represents the completed animal form. Always temporary: it cannot be made permanent.
 *
 * @author Azami7
 */
public class ANIMAGUS_INCANTATION extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the incantation state in game ticks
     * @param isPermanent ignored - animagus incantation is always temporary
     * @param pid         the unique ID of the player reciting the Animagus incantation
     */
    public ANIMAGUS_INCANTATION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, false, pid);

        effectType = O2EffectType.ANIMAGUS_INCANTATION;
        checkDurationBounds();
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    @Override
    public void doRemove() {
    }

    /**
     * No-op: this effect is always temporary and ignores requests to make it permanent.
     *
     * @param perm ignored
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}