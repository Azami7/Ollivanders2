package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;

import org.jetbrains.annotations.NotNull;

/**
 * Grants the target flight for the effect's duration. On removal, flight is revoked unless the player has the
 * {@code Ollivanders2.admin} permission. Subclasses may enable a smoke particle trail via {@link #doSmokeEffect}.
 *
 * @author Azami7
 */
public class FLYING extends O2Effect {
    /**
     * When true, a smoke particle trail is played at the player's location each tick during flight.
     */
    boolean doSmokeEffect = false;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to grant flight
     */
    public FLYING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FLYING;
        checkDurationBounds();
    }

    /**
     * Age the effect, enable flight, and optionally play smoke particles.
     */
    @Override
    public void checkEffect() {
        age(1);

        if (!target.getAllowFlight()) {
            common.printDebugMessage("Adding flight for " + target.getDisplayName(), null, null, false);
            target.setAllowFlight(true);
        }
        if (doSmokeEffect)
            target.getWorld().playEffect(target.getLocation(), org.bukkit.Effect.SMOKE, 4);
    }

    /**
     * Revoke flight ability when the effect is removed unless the player has the Ollivanders2.admin permission.
     */
    @Override
    public void doRemove() {
        if (!target.hasPermission("Ollivanders2.admin")) {
            common.printDebugMessage("Removing flight for " + target.getDisplayName(), null, null, false);
            target.setAllowFlight(false);
            target.setFlying(false);
        }
    }
}