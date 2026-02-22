package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a scheduled divination prophecy with delayed effect execution.
 * <p>
 * An O2Prophecy encapsulates all the information needed to execute a prophecy at a future time:
 * the target player, the magical effect to apply, the accuracy rating, and the scheduled execution time.
 * Prophecies are processed each game tick: when the time reaches zero, the prophecy is "fulfilled" by
 * applying the magical effect to the target player. The accuracy rating determines whether the prophecy
 * actually succeeds when its time arrives—a random check compares the prophecy's accuracy against a
 * random 0-99 roll.
 * </p>
 * <p>
 * Prophecies can be handled in two ways:
 * </p>
 * <ul>
 * <li><strong>Online Target:</strong> If the target player is online when the prophecy is fulfilled,
 *     the effect is applied directly and a broadcast message announces the prophecy's outcome.</li>
 * <li><strong>Offline Target:</strong> If the target player is offline, the prophecy is stashed and
 *     automatically re-executed when the player logs back in.</li>
 * </ul>
 *
 * @author Azami7
 * @see O2Divination for the divination system that creates prophecies
 * @see O2Prophecies for the container managing all active prophecies
 */
public class O2Prophecy {
    /**
     * Reference to the plugin for accessing configuration, logging, and server API.
     */
    final private Ollivanders2 p;

    /**
     * Maximum accuracy percentage a prophecy can have.
     * Used to clamp prophecy accuracy in the constructor. Represents a 99% success rate.
     */
    static public final int maxAccuracy = 99;

    /**
     * The type of magical effect that will be applied when this prophecy is fulfilled.
     * Used to instantiate the actual effect object when the prophecy is executed.
     */
    private final O2EffectType effectType;

    /**
     * The unique identifier of the target player who will be affected by this prophecy.
     * The effect will be applied to this player when the prophecy is fulfilled.
     */
    private final UUID targetID;

    /**
     * The unique identifier of the prophet (player who cast the prophecy spell).
     * Used to notify the prophet of the prophecy's success or failure.
     */
    private final UUID prophetID;

    /**
     * The time remaining until the prophecy is fulfilled, measured in game ticks.
     * One game tick = 1/20 of a second (50 milliseconds).
     * This value is decremented by 1 each game tick via the age() method.
     * When time reaches zero or below, the prophecy is ready to be fulfilled.
     */
    private long time;

    /**
     * The duration of the magical effect in game ticks.
     * Determines how long the effect will persist after the prophecy is fulfilled.
     * Range is typically 600-12000 ticks (30 seconds - 10 minutes).
     */
    private final int duration;

    /**
     * The success probability of this prophecy as a percentage (0-99).
     * When the prophecy is fulfilled, a random 0-99 roll is compared against this value.
     * If accuracy > roll, the prophecy succeeds; otherwise it fails.
     */
    private final int accuracy;

    /**
     * The human-readable prophecy message displayed to players when fulfilled.
     * Example: "At sunset tomorrow, Fred will fall in to a deep sleep."
     */
    private final String prophecyMessage;

    /**
     * Flag indicating whether this prophecy has been expired and should not be processed further.
     * Once killed, the prophecy will not be fulfilled even if time reaches zero.
     */
    private boolean kill = false;

    /**
     * Constructor for creating a new divination prophecy.
     *
     * <p>Creates a prophecy with the given parameters. Accuracy is automatically clamped to 0-99 range.
     * Once created, the prophecy will be managed by O2Prophecies and processed each game tick.</p>
     *
     * @param plugin         a reference to the plugin for API access
     * @param effectType     the magical effect that will be applied if the prophecy succeeds
     * @param message        the human-readable prophecy message displayed to players
     * @param targetID       the unique ID of the target player who will receive the effect
     * @param prophetID      the unique ID of the prophet (caster) for notification purposes
     * @param delayTime      the number of game ticks until the prophecy is fulfilled
     * @param effectDuration the duration of the effect in game ticks (600-12000 typical)
     * @param accuracy       the success probability (0-99). Values outside this range are clamped
     */
    public O2Prophecy(@NotNull Ollivanders2 plugin, @NotNull O2EffectType effectType, @NotNull String message, @NotNull UUID targetID, @NotNull UUID prophetID, long delayTime, int effectDuration, int accuracy) {
        p = plugin;
        this.effectType = effectType;
        this.targetID = targetID;
        this.prophetID = prophetID;
        this.prophecyMessage = message;
        this.time = delayTime;
        this.duration = effectDuration;

        if (accuracy > maxAccuracy) // accuracy cannot be higher than maxAccuracy
            this.accuracy = maxAccuracy;
        else if (accuracy < 0) // accuracy cannot be negative
            this.accuracy = 0;
        else
            this.accuracy = accuracy;
    }

    /**
     * Get the effect this prophecy causes.
     *
     * @return the effect type
     */
    @NotNull
    public O2EffectType getEffect() {
        return effectType;
    }

    /**
     * Get the ID of target player for this prophecy
     *
     * @return the target player's unique ID
     */
    @NotNull
    public UUID getTargetID() {
        return targetID;
    }

    /**
     * Get the ID of player who made this prophecy
     *
     * @return the prophet player's unique ID
     */
    @NotNull
    public UUID getProphetID() {
        return prophetID;
    }

    /**
     * Get the time until this prophecy happens
     *
     * @return the time in game ticks
     */
    public long getTime() {
        return time;
    }

    /**
     * Get the duration of the effect from this prophecy
     *
     * @return the duration in game ticks
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the prophecy message, i.e. "After the sun sets on the 3rd day, Fred will fall in to a deep sleep."
     *
     * @return the prophecy message
     */
    public String getProphecyMessage() {
        return prophecyMessage;
    }

    /**
     * Get the percent accuracy of this prophecy
     *
     * @return the accuracy percent 0-99
     */
    int getAccuracy() {
        return accuracy;
    }

    /**
     * Is this prophecy expired/killed
     *
     * @return true if killed, false otherwise
     */
    public boolean isKilled() {
        return kill;
    }

    /**
     * Decrement the prophecy time by 1 game tick.
     *
     * <p>Called by O2Prophecies.upkeep() each game tick to advance the prophecy countdown.
     * When time reaches zero or below, the prophecy is ready to be fulfilled.</p>
     */
    public void age() {
        time = time - 1;
    }

    /**
     * Mark this prophecy as expired and prevent further processing.
     *
     * <p>Once killed, the prophecy will not be fulfilled even if it was scheduled to be,
     * and will be removed from the prophecy lists during the next O2Prophecies.upkeep() call.</p>
     */
    public void kill() {
        kill = true;
    }

    /**
     * Execute this prophecy and apply its magical effect to the target player.
     *
     * <p>This method handles the complete prophecy fulfillment workflow:</p>
     * <ol>
     * <li>Checks if the prophecy has already been killed/expired; returns early if so</li>
     * <li>Locates the target player on the server</li>
     * <li>If target is offline: stashes the prophecy for later execution when the player logs in</li>
     * <li>If target is online: performs an accuracy check by comparing the prophecy's accuracy
     *     against a random 0-99 roll</li>
     * <li>On success (accuracy > roll): instantiates the magical effect and applies it to the target,
     *     then broadcasts a message to all players announcing the prophecy's fulfillment</li>
     * <li>On failure (accuracy &le; roll): sends a message to the prophet informing them the prophecy failed</li>
     * <li>Marks the prophecy as killed to prevent further execution</li>
     * </ol>
     *
     * <p>The accuracy check is probabilistic: a prophecy with 50% accuracy has a 50% chance of success,
     * and a prophecy with 99% accuracy has a 99% chance of success.</p>
     */
    public void fulfill() {
        if (Ollivanders2.debug) {
            p.getLogger().info("Fulfilling prophecy");
        }

        // do nothing if the prophecy is already killed
        if (kill) {
            return;
        }

        Player target = p.getServer().getPlayer(targetID);

        if (target == null) {
            // player is offline, stash this prophecy for when the player returns
            Ollivanders2API.getProphecies().addOfflineProphecy(this);
            return;
        }

        // Perform accuracy check: generate random 0-99 value and compare against prophecy accuracy
        // Example: 50% accuracy needs to roll < 50 to succeed
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        if (accuracy > rand || (Ollivanders2.testMode && Ollivanders2.maxSpellLevel)) { // make sure this always succeeds in test mode
            // Prophecy succeeds: apply the effect to the target
            O2Effect effect;
            Class<?> effectClass = effectType.getClassName();

            try {
                // Use reflection to dynamically instantiate the effect class without a factory method
                // Constructor signature: (Ollivanders2, int duration, boolean isPermanent, UUID targetID)
                // prophecy effects are always temporary so isPermanent is always false
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, boolean.class, UUID.class).newInstance(p, duration, false, targetID);
            }
            catch (Exception e) {
                e.printStackTrace();
                kill();
                return;
            }

            // Ensure the effect is not permanent - prophecy effects have a limited duration
            effect.setPermanent(false);
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            O2Player prophet = Ollivanders2API.getPlayers().getPlayer(prophetID);
            if (prophet != null) {
                String prophetName = prophet.getPlayerName();
                p.getServer().broadcastMessage(Ollivanders2.chatColor + "And so came to pass the prophecy of " + prophetName + ", \"" + prophecyMessage + "\"");
            }
        }
        else {
            Player prophet = p.getServer().getPlayer(prophetID);
            if (prophet != null)
                prophet.sendMessage(Ollivanders2.chatColor + "Your prophecy, \"" + prophecyMessage + "\" did not come to pass.");
        }

        kill();
    }
}
