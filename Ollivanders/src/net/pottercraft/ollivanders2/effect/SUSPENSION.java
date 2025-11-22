package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Suspension effect that hoists the affected player into the air and maintains them there.
 *
 * <p>SUSPENSION is a debilitating effect that suspends the target player approximately 1.5 blocks
 * into the air by teleporting them to a suspended location. The effect applies two additional effects
 * to maintain the suspension state: FLYING (to prevent falling due to gravity) and IMMOBILIZE (to
 * prevent any movement that could break the suspension). Both secondary effects have durations slightly
 * longer than the main effect to ensure the player remains suspended throughout. The player's original
 * location is recorded and restored when the effect is removed. This effect replaced the original
 * LEVICORPUS implementation.</p>
 *
 * <p>Suspension Configuration:</p>
 * <ul>
 * <li>Suspension height: 1.5 blocks above eye level</li>
 * <li>Head pitch: 45 degrees (downward tilt)</li>
 * <li>Secondary effects: FLYING (prevents falling) and IMMOBILIZE (prevents movement)</li>
 * <li>Secondary effect duration: main duration + 10 ticks</li>
 * <li>Suspension state: tracked via boolean flag</li>
 * <li>Velocity events: cancelled to maintain suspension</li>
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
    }

    /**
     * Age the suspension effect and initialize suspension on first tick.
     *
     * <p>Called each game tick. This method ages the effect counter and checks if the player has been
     * suspended yet. If not, it calls suspend() to hoist the player into the air and apply secondary
     * effects on the first activation.</p>
     */
    @Override
    public void checkEffect() {
        age(1);

        if (!suspended)
            suspend();
    }

    /**
     * Hoist the player into the air and apply suspension effects.
     *
     * <p>Teleports the player to a suspension location approximately 1.5 blocks above their eye level,
     * records their original location for later restoration, applies FLYING and IMMOBILIZE effects to
     * maintain the suspension state, and marks the suspension flag as initialized. If the player is
     * offline when this is called, the effect is terminated.</p>
     */
    private void suspend() {
        Player target = p.getServer().getPlayer(targetID);
        if (target == null) {
            kill();
            return;
        }

        addAdditionalEffects();

        // suspend them in the air
        originalLocation = target.getLocation();
        Location newLoc = target.getEyeLocation();
        Location suspendLoc = new Location(newLoc.getWorld(), newLoc.getX(), newLoc.getY(), newLoc.getZ(), originalLocation.getYaw(), 45);
        target.teleport(suspendLoc);

        suspended = true;
    }

    /**
     * Apply secondary effects necessary to maintain suspension.
     *
     * <p>Applies two effects to keep the player suspended: FLYING (prevents falling from gravity) with
     * duration extended by 10 ticks to outlast the main effect, and IMMOBILIZE (prevents all movement)
     * also with extended duration. These effects work together to ensure the player remains suspended
     * and unable to escape the suspension state.</p>
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
        Player target = p.getServer().getPlayer(targetID);
        if (target == null)
            return;

        // teleport them back to their original location
        target.teleport(originalLocation);

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
}