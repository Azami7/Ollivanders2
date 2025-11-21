package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
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
 * <p>Mechanism:</p>
 * <ul>
 * <li>Blocks all sleep state changes by cancelling PlayerBedEnterEvent</li>
 * <li>Detectable by both information spells (Informous) and mind-reading spells (Legilimens)</li>
 * <li>Detection text: "is unnaturally alert" - indicating forced wakefulness</li>
 * <li>Expires naturally when duration reaches zero; no special cleanup required</li>
 * </ul>
 *
 * @author Azami7
 */
public class AWAKE extends O2Effect {
    /**
     * Constructor for creating an awake-state effect.
     *
     * <p>Creates a temporary effect that prevents the player from sleeping for the specified duration.
     * Sets both information and mind-reading detection texts to indicate the player is unnaturally alert.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the awake state in game ticks
     * @param pid      the unique ID of the player to keep awake
     */
    public AWAKE(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.AWAKE;
        informousText = legilimensText = "is unnaturally alert";
    }

    /**
     * Age the awake effect to track its remaining duration.
     *
     * <p>Called each game tick to decrement the effect's duration. The actual sleep prevention is handled
     * by the event handler method (doOnPlayerBedEnterEvent). When the duration reaches zero, the effect
     * is automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
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
        common.printDebugMessage("AWAKE: cancelling PlayerBedEnterEvent", null, null, false);
    }
}
