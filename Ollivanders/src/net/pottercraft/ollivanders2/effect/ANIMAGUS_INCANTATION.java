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
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the incantation state in game ticks
     * @param pid      the unique ID of the player reciting the Animagus incantation
     */
    public ANIMAGUS_INCANTATION(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.ANIMAGUS_INCANTATION;
    }

    /**
     * No active processing for incantation state.
     *
     * <p>This incantation marker effect is passive and does not perform any active behavior during each tick.
     * The state is managed externally by spell completion logic or natural expiration when the duration reaches zero.
     * To complete or modify the incantation, the player must interact with the spell system directly
     * (e.g., repeating the incantation).</p>
     */
    @Override
    public void checkEffect() {
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
}