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
 * <p>SUSPENSION is a debilitating effect that suspends the target player in mid-air by teleporting
 * them to a suspended location 1 block above their starting position. The effect applies two additional
 * permanent effects to maintain the suspension state: FLYING (to prevent falling due to gravity) and
 * FULL_IMMOBILIZE (to prevent any movement or teleportation that could break the suspension). The
 * player's original location is recorded and restored when the effect is removed. This effect replaced
 * the original LEVICORPUS implementation.</p>
 *
 * <p>Suspension Configuration:</p>
 * <ul>
 * <li>Suspension height: 1 block above the player's starting position</li>
 * <li>Head pitch: 45 degrees (downward tilt)</li>
 * <li>Secondary effects: FLYING (permanent, prevents falling) and FULL_IMMOBILIZE (permanent, prevents all movement)</li>
 * <li>Suspension initialization: delayed 5 ticks after effect activation to allow FLYING to activate before teleport</li>
 * <li>Immobilization timing: FULL_IMMOBILIZE is added after teleport to avoid cancelling the suspension teleport</li>
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
     * <li>Immediately adds the FLYING effect to allow it to activate</li>
     * <li>Delays the actual suspension (teleport and setFlying) by 5 ticks to ensure the FLYING effect
     * has time to activate before the player is moved</li>
     * </ol>
     *
     * <p>This delay is necessary because the FLYING effect must be initialized before calling setFlying(true),
     * otherwise the player could briefly fall before the effect prevents gravity. The FULL_IMMOBILIZE effect
     * is added after the teleport (in the suspend() method) to prevent it from cancelling the teleport event.</p>
     */
    @Override
    public void checkEffect() {
        age(1);

        if (!suspended) {
            // we need to add the additional effects first so that flying gets enabled for the player before we then try to set them flying
            addAdditionalEffects();

            // delay the suspend action until the other effects have time to start working
            suspended = true;

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
     * <p>Teleports the player to a suspension location 1 block above their original position with a head
     * pitch of 45 degrees (downward tilt), records their original location for later restoration, and
     * enables flying mode to prevent falling. The FULL_IMMOBILIZE effect is added after the teleport to
     * prevent it from cancelling the suspension teleport event. This method is called 5 ticks after
     * checkEffect() to ensure the FLYING effect has time to activate before the player is moved and set
     * to flying mode.</p>
     */
    private void suspend() {
        // we need to add the additional effects first so that flying gets enabled for the player before we then try to set them flying
        addAdditionalEffects();

        // suspend them in the air and set them flying so they don't fall from the new location
        originalLocation = target.getLocation();
        Location suspendLoc = new Location(originalLocation.getWorld(), originalLocation.getX(), originalLocation.getY() + 1, originalLocation.getZ(), originalLocation.getYaw(), 45);

        target.setFlying(true);
        target.teleport(suspendLoc);

        // add an immobilize effect so they cannot move
        // we have to add this here because otherwise FULL_IMMOBILIZE will cancel the player teleport
        FULL_IMMOBILIZE fullImmobilize = new FULL_IMMOBILIZE(p, 5, true, targetID);
        Ollivanders2API.getPlayers().playerEffects.addEffect(fullImmobilize);
        additionalEffects.add(fullImmobilize.effectType);

        suspended = true;
    }

    /**
     * Apply the FLYING effect necessary to maintain suspension.
     *
     * <p>Applies the FLYING effect as a permanent effect to prevent the player from falling due to gravity.
     * This effect must be initialized early (in checkEffect()) so that it has time to activate before the
     * player is teleported and set to flying mode. The FULL_IMMOBILIZE effect is added separately in the
     * suspend() method after the teleport to prevent it from cancelling the suspension teleport event.</p>
     */
    private void addAdditionalEffects() {
        // make them fly so they do not fall from suspension
        FLYING flying = new FLYING(p, 5, true, targetID);
        Ollivanders2API.getPlayers().playerEffects.addEffect(flying);
        additionalEffects.add(flying.effectType);
    }

    /**
     * Clean up the suspension effect and restore the player to normal state.
     *
     * <p>When the suspension effect is removed, this method disables flying mode and removes the
     * secondary effects (FLYING and FULL_IMMOBILIZE) that were applied to maintain suspension. The player
     * is responsible for handling their own descent or location after the suspension ends.</p>
     */
    @Override
    public void doRemove() {
        // turn off flying
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
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

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