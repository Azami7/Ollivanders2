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
 * Suspends the affected player one block above their starting position, tilting their head 45 degrees down. Applies
 * companion permanent {@link FLYING} (prevents falling) and {@link FULL_IMMOBILIZE} (prevents movement) effects to
 * hold them there, and restores the original location on removal.
 *
 * @author Azami7
 */
public class SUSPENSION extends O2Effect {
    /**
     * The player's location before suspension, recorded at suspend time and restored on removal.
     */
    Location originalLocation;

    /**
     * Whether the player has been hoisted into the air yet; ensures suspension runs exactly once.
     */
    boolean suspended = false;

    /**
     * The companion effects (FLYING, FULL_IMMOBILIZE) added to hold the suspension, removed on cleanup.
     */
    final ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

    /**
     * Constructor
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
     * On the first tick, adds the FLYING effect and schedules the suspend 5 ticks later so FLYING is active before the
     * teleport; otherwise the player would briefly fall. Ages the effect each tick.
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
     * Records the player's location, teleports them one block up with a 45-degree downward head pitch, sets them
     * flying, and adds the FULL_IMMOBILIZE effect. FULL_IMMOBILIZE is added here rather than earlier so it does not
     * cancel this teleport.
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
     * Adds the permanent FLYING companion effect that keeps the player from falling once suspended.
     */
    private void addAdditionalEffects() {
        // make them fly so they do not fall from suspension
        FLYING flying = new FLYING(p, 5, true, targetID);
        Ollivanders2API.getPlayers().playerEffects.addEffect(flying);
        additionalEffects.add(flying.effectType);
    }

    /**
     * Disables flying and removes the companion FLYING and FULL_IMMOBILIZE effects.
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
     * Cancels the target's velocity events so nothing pushes them out of suspension.
     *
     * @param event the player velocity event
     */
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("SUSPENSION: cancelling PlayerVelocityEvent", null, null, false);
    }

    /**
     * @return true once the player has been hoisted into the air, false while suspension is still pending
     */
    public boolean isSuspended() {
        return suspended;
    }
}