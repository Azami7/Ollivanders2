package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Suspension effect that hoists the affected player into the air and maintains them there.
 *
 * <p>SUSPENSION is a debilitating effect that suspends the target player at eye level by teleporting
 * them to a suspended location. The effect applies two additional permanent effects to maintain the
 * suspension state: FLYING (to prevent falling due to gravity) and IMMOBILIZE (to prevent any movement
 * that could break the suspension). The player's original location is recorded and restored when the
 * effect is removed. This effect replaced the original LEVICORPUS implementation.</p>
 *
 * <p>Suspension Configuration:</p>
 * <ul>
 * <li>Suspension height: at player eye level</li>
 * <li>Head pitch: 45 degrees (downward tilt)</li>
 * <li>Secondary effects: FLYING (permanent, prevents falling) and IMMOBILIZE (permanent, prevents movement)</li>
 * <li>Suspension initialization: delayed 5 ticks after effect activation to allow secondary effects to activate</li>
 * <li>Suspension state: tracked via boolean flag</li>
 * <li>Velocity events: cancelled to maintain suspension stability</li>
 * </ul>
 *
 * @author Azami7
 */
public class SUSPENSION extends O2Effect {
    /**
     * the original location of the player
     */
    Location originalLocation;

    /**
     * are they currently suspended
     */
    boolean suspended = false;

    /**
     * additional effect such as immobilization
     */
    final ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

    /**
     * Constructor for creating a suspension effect.
     *
     * <p>Creates a suspension effect that will hoist the target player into the air on the first
     * checkEffect() call. The player will be teleported to a suspended location and secondary effects
     * (FLYING and IMMOBILIZE) will be applied to maintain the suspension state. The original location
     * is recorded for restoration when the effect is removed.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the suspension effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to suspend
     */
    public SUSPENSION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SUSPENSION;
        checkDurationBounds();
    }

    /**
     * Age the suspension effect and initialize suspension on first tick.
     *
     * <p>Called each game tick. This method ages the effect counter and checks if the player has been
     * suspended yet via the suspended flag. If not, it performs the suspension initialization in two steps:</p>
     * <ol>
     * <li>Immediately adds the secondary effects (FLYING and IMMOBILIZE) to allow them to activate</li>
     * <li>Delays the actual suspension (teleport and setFlying) by 5 ticks to ensure the secondary effects
     * have time to start working before the player is moved</li>
     * </ol>
     *
     * <p>This delay is necessary because the FLYING effect must be initialized before calling setFlying(true),
     * otherwise the player could briefly fall before the effect prevents gravity.</p>
     */
    @Override
    public void checkEffect() {
        age(1);

        if (!suspended) {
            // we need to add the additional effects first so that flying gets enabled for the player before we then try to set them flying
            addAdditionalEffects();

            // delay the suspend action until the other effects have time to start working
            new BukkitRunnable() {
                @Override
                public void run() {
                    suspend();
                }
            }.runTaskLater(p, 5);
        }
    }

    /**
     * Hoist the player into the air and apply suspension effects.
     *
     * <p>Teleports the player to a suspension location at their eye level with a head pitch of 45 degrees,
     * records their original location for later restoration, enables flying mode to prevent falling,
     * and marks the suspension flag as initialized. This method is called 5 ticks after checkEffect()
     * to ensure secondary effects have activated. If the player is offline when this is called, the
     * effect is terminated.</p>
     */
    private void suspend() {
        // suspend them in the air and set them flying so they don't fall from the new location
        originalLocation = target.getLocation();
        Location newLoc = target.getEyeLocation();
        Location suspendLoc = new Location(newLoc.getWorld(), newLoc.getX(), newLoc.getY(), newLoc.getZ(), originalLocation.getYaw(), 45);

        target.setFlying(true);
        target.teleport(suspendLoc);

        suspended = true;
    }

    /**
     * Apply secondary effects necessary to maintain suspension.
     *
     * <p>Applies two permanent effects to keep the player suspended: FLYING (prevents falling from gravity)
     * and IMMOBILIZE (prevents all movement). Both effects are created as permanent to ensure the player
     * remains suspended and unable to escape the suspension state. These secondary effects are tracked in
     * the additionalEffects list so they can be removed when the suspension effect is cleaned up via doRemove().</p>
     */
    private void addAdditionalEffects() {
        // make them fly so they do not fall from suspension
        FLYING flying = new FLYING(p, 5, true, targetID);
        Ollivanders2API.getPlayers().playerEffects.addEffect(flying);
        additionalEffects.add(O2EffectType.FLYING);

        // add an immobilize effect so they cannot move
        IMMOBILIZE immobilize = new IMMOBILIZE(p, 5, true, targetID);
        Ollivanders2API.getPlayers().playerEffects.addEffect(immobilize);
        additionalEffects.add(O2EffectType.IMMOBILIZE);
    }

    /**
     * Clean up the suspension effect and restore the player to normal state.
     *
     * <p>When the suspension effect is removed, this method teleports the player back to their original
     * location and removes the secondary effects (FLYING and IMMOBILIZE) that were applied to maintain
     * suspension. If the player is offline, cleanup is gracefully skipped.</p>
     */
    @Override
    public void doRemove() {
        // teleport them back to their original location and turn off flying
        target.teleport(originalLocation);
        target.setFlying(false);

        // remove flying and immobilize effects
        for (O2EffectType effectType : additionalEffects) {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
        }
    }

    /**
     * Cancel velocity changes to maintain suspension stability.
     *
     * <p>Cancels all player velocity events that would otherwise change the player's motion, ensuring
     * they remain suspended in their designated location without falling or being pushed away.</p>
     *
     * @param event the player velocity event to cancel
     */
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("SUSPENSION: cancelling PlayerVelocityEvent", null, null, false);
    }

    /**
     * Check if the player is currently suspended in the air.
     *
     * <p>Returns true after the player has been hoisted into the air via the suspend() method.
     * This flag is used to ensure the suspension mechanism runs exactly once per effect.</p>
     *
     * @return true if the player has been suspended, false if the suspension is still pending
     */
    public boolean isSuspended() {
        return suspended;
    }
}