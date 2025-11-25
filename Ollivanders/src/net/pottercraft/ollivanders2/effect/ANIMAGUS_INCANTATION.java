package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Temporary marker effect indicating the player is currently reciting the Animagus incantation.
 *
 * <p>ANIMAGUS_INCANTATION is a temporary effect that marks an active Animagus transformation spell casting
 * session. Unlike {@link ANIMAGUS_EFFECT} (which represents the permanent animal form transformation),
 * this effect simply tracks that the player is in the process of reciting the incantation. The duration
 * of this effect corresponds to the time the player has to continue reciting the spell before completion
 * or expiration.</p>
 *
 * <p>This effect serves as a state marker for spell progression systems and prevents multiple simultaneous
 * incantation attempts. When the incantation is complete (or expires), this effect is removed and may be
 * replaced by an ANIMAGUS_EFFECT if the spell succeeds.</p>
 *
 * @author Azami7
 */
public class ANIMAGUS_INCANTATION extends O2Effect {
    /**
     * Constructor for creating an Animagus incantation marker effect.
     *
     * <p>Creates a temporary effect that marks the player as actively reciting the Animagus spell.
     * The duration parameter specifies how long the incantation remains active before expiring.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the incantation state in game ticks
     * @param isPermanent ignored - animagus incantation is always temporary
     * @param pid         the unique ID of the player reciting the Animagus incantation
     */
    public ANIMAGUS_INCANTATION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, false, pid);

        effectType = O2EffectType.ANIMAGUS_INCANTATION;
    }

    /**
     * Age the incantation effect to track its remaining duration.
     *
     * <p>Called every game tick by the effect system. Decrements the effect's duration by 1 tick.
     * When the duration reaches zero or below, the effect is automatically killed and removed.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the incantation effect is removed.
     *
     * <p>The default implementation does nothing, as the incantation marker effect has no state to clean up.
     * Removal of this effect simply ends the incantation state, allowing the player to attempt the spell again
     * or move on to other actions.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Overridden to prevent permanent status - animagus incantation is always temporary.
     *
     * <p>ANIMAGUS_INCANTATION enforces non-permanent behavior by ignoring all setPermanent() calls.
     * This effect is designed as a temporary marker for the incantation spell casting state and cannot
     * be converted to permanent status. Attempts to set this effect to permanent are silently ignored.</p>
     *
     * @param perm the requested permanent status (ignored - effect remains temporary regardless)
     */
    @Override
    public void setPermanent(boolean perm) {}
}