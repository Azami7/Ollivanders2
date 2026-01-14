package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Temporary effect that prevents the player from sleeping, blocking sleep-inducing abilities.
 *
 * <p>The AWAKE effect is a temporary counter-effect that keeps a player awake and prevents them from
 * entering a sleeping state. This is typically used to counteract sleep-inducing spells or effects that
 * would otherwise put the player to sleep. The effect persists for its specified duration and automatically
 * expires when the duration reaches zero.</p>
 *
 * <p>Effect Lifecycle:</p>
 * <ol>
 * <li>On each game tick, checkEffect() checks if the player is sleeping and wakes them if needed</li>
 * <li>If the player has a SLEEPING effect active, AWAKE removes it (AWAKE overrides SLEEPING)</li>
 * <li>PlayerBedEnterEvent is cancelled to prevent the player from entering beds</li>
 * <li>When the effect expires, the player can naturally sleep again; no cleanup required</li>
 * </ol>
 *
 * <p>Interaction with SLEEPING:</p>
 * <ul>
 * <li>AWAKE and SLEEPING are mutually exclusive - only one can be active at a time</li>
 * <li>If SLEEPING detects AWAKE, it immediately kills itself</li>
 * <li>If AWAKE detects SLEEPING, it removes that effect and wakes the player</li>
 * <li>This bidirectional interaction prevents conflicting sleep states</li>
 * </ul>
 *
 * <p>Detection:</p>
 * <ul>
 * <li>Detectable by the Informous spell with text: "is unnaturally alert"</li>
 * <li>Detectable by the Legilimens spell with text: "is unnaturally alert"</li>
 * </ul>
 *
 * @author Azami7
 * @see SLEEPING for the sleep-inducing effect that AWAKE counters
 */
public class AWAKE extends O2Effect {
    /**
     * Constructor for creating an awake-state effect.
     *
     * <p>Creates an effect that prevents the player from sleeping for the specified duration.
     * Sets both information and mind-reading detection texts to indicate the player is unnaturally alert.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the awake state in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to keep awake
     */
    public AWAKE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.AWAKE;
        checkDurationBounds();

        informousText = legilimensText = "is unnaturally alert";
    }

    /**
     * Check the awake effect each game tick and wake sleeping players.
     *
     * <p>Called each game tick to decrement the effect's duration and ensure the player remains awake.
     * This method performs three key functions:</p>
     * <ol>
     * <li>Ages the effect by 1 tick (when duration reaches zero, the effect is automatically killed)</li>
     * <li>Removes any active SLEEPING effect to enforce AWAKE override (only if SLEEPING is currently active)</li>
     * <li>Wakes the player if they are sleeping (skipped in test mode to avoid MockBukkit limitations)</li>
     * </ol>
     *
     * <p>The active waking mechanism ensures that even if a SLEEPING effect was applied before AWAKE,
     * the player will be immediately woken on the next game tick. This works in conjunction with
     * doOnPlayerBedEnterEvent() which prevents new sleep attempts. In test mode, the wakeup() call is skipped
     * because MockBukkit does not implement player sleep functionality.</p>
     */
    @Override
    public void checkEffect() {
        age(1);

        // AWAKE overrides SLEEPING so remove it if the player has it
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(targetID, O2EffectType.SLEEPING))
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.SLEEPING);

        // make sure target is not sleeping
        if (target.isSleeping()) {
            if (!Ollivanders2.testMode) // MockBukkit does not have player sleep and doing this will cause an UnimplementedOperationException
                target.wakeup(true);
        }
    }

    /**
     * Perform cleanup when the awake effect is removed.
     *
     * <p>The default implementation does nothing, as the awake effect is passive and has no state
     * to clean up. Simply allowing the player to sleep again is handled automatically by effect removal.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Prevent the player from entering sleep state.
     *
     * <p>Called when the player attempts to enter a bed. This method cancels the bed enter event,
     * preventing the player from sleeping while this awake effect is active. This is the core mechanism
     * that keeps the player awake by blocking all sleep state transitions.</p>
     *
     * @param event the player bed enter event to cancel
     */
    @Override
    void doOnPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }
}
