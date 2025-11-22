package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Effect that grants temporary flight ability to a player.
 *
 * <p>The FLYING effect enables the target player to fly for the specified duration. Each game tick,
 * the effect checks the remaining duration and maintains flight state: while duration > 1 tick remains,
 * the player can fly; on the final tick (duration ≤ 1), flight is disabled. Optional smoke particle
 * effects can be displayed during flight by setting doSmokeEffect to true (default is false).</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Flight enabled each tick while duration > 1</li>
 * <li>Flight disabled on final tick (duration ≤ 1) before effect expires</li>
 * <li>Optional smoke particle effects each tick (disabled by default)</li>
 * <li>On removal: flight revoked unless player has Ollivanders2.admin permission</li>
 * <li>Admin players retain flight ability through spell/effect removal</li>
 * </ul>
 *
 * @author Azami7
 */
public class FLYING extends O2Effect {
    /**
     * Flag controlling whether to display smoke particle effects during flight.
     *
     * <p>When true, SMOKE particle effects (intensity 4) are played at the player's location each tick
     * while the player is flying (duration > 1). This creates a visual trail during flight. Default is false
     * to avoid excessive particle effects. Subclasses can set this to true for visually distinctive flight
     * (e.g., BROOM_FLYING sets this to false for clean broom flight, while regular FLYING has smoke disabled).</p>
     */
    boolean doSmokeEffect = false;

    /**
     * Constructor for creating a flight effect.
     *
     * <p>Creates an effect that grants temporary flight ability to the target player. The effect will enable
     * flight each tick while the duration remains above 1 tick, then disable flight on the final tick before
     * expiring. Smoke effects are disabled by default (doSmokeEffect = false) but can be enabled by subclasses
     * for visually distinctive flight mechanics.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to grant flight
     */
    public FLYING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FLYING;
    }

    /**
     * Enable flight while the effect is active and disable on final tick.
     *
     * <p>Called each game tick. This method:</p>
     * <ol>
     * <li>Ages the effect (decrements duration)</li>
     * <li>If target player not found: kills the effect</li>
     * <li>If duration > 1 (effect still active):
     *   <ul>
     *   <li>Enables flight (setAllowFlight(true))</li>
     *   <li>Plays smoke effect each tick if doSmokeEffect is true</li>
     *   </ul>
     * </li>
     * <li>If duration ≤ 1 (final tick):
     *   <ul>
     *   <li>Disables flight (setAllowFlight(false))</li>
     *   <li>Effect expires naturally on next tick</li>
     *   </ul>
     * </li>
     * </ol>
     */
    @Override
    public void checkEffect() {
        Player target = p.getServer().getPlayer(targetID);

        if (target != null) {
            age(1);
            if (duration > 1) {
                target.setAllowFlight(true);
                if (doSmokeEffect)
                    target.getWorld().playEffect(target.getLocation(), org.bukkit.Effect.SMOKE, 4);
            }
            else {
                common.printDebugMessage("Adding flight for " + target.displayName(), null, null, false);
                target.setAllowFlight(false);
            }
        }
        else
            kill();
    }

    /**
     * Revoke flight ability when the effect is removed (unless player is an admin).
     *
     * <p>Called when the flight effect expires or is manually killed. This method checks if the player has
     * the "Ollivanders2.admin" permission:
     * <ul>
     * <li>Non-admin players: flight ability is revoked (setAllowFlight(false) and setFlying(false))</li>
     * <li>Admin players: flight ability is preserved - admins can retain flight through spell/effect removal</li>
     * </ul>
     * <p>This allows server admins to maintain flight privileges when flight-granting spells expire.</p>
     */
    @Override
    public void doRemove() {
        Player player = p.getServer().getPlayer(targetID);
        if (player != null) {
            // if the player is not an admin, remove flight ability
            if (!player.hasPermission("Ollivanders2.admin")) {
                common.printDebugMessage("Removing flight for " + player.displayName(), null, null, false);
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
    }
}