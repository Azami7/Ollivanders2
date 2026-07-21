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
 * A single scheduled divination prophecy: an effect to apply to a target player at a future time, with an accuracy
 * that determines whether it actually comes to pass.
 * <p>
 * When fulfilled while the target is online, a probabilistic accuracy check decides success; on success the effect is
 * applied and the outcome is broadcast, on failure the prophet is privately told it did not come to pass. If the target
 * is offline at fulfillment time, the prophecy is stashed by {@link O2Prophecies} and retried when they rejoin.
 * </p>
 *
 * @author Azami7
 * @see O2Divination
 * @see O2Prophecies
 */
public class O2Prophecy {
    /**
     * Reference to the plugin
     */
    final private Ollivanders2 p;

    /**
     * The maximum accuracy percentage a prophecy can have; accuracy is clamped to this in the constructor.
     */
    static public final int maxAccuracy = 99;

    /**
     * The effect applied to the target when this prophecy is fulfilled.
     */
    private final O2EffectType effectType;

    /**
     * The target player the effect is applied to on fulfillment.
     */
    private final UUID targetID;

    /**
     * The prophet who made this prophecy, notified of its success or failure.
     */
    private final UUID prophetID;

    /**
     * Game ticks remaining until fulfillment; decremented each tick by {@link #age()} and fulfilled once it reaches zero.
     */
    private long time;

    /**
     * How long the effect lasts once applied, in game ticks.
     */
    private final int duration;

    /**
     * The success probability as a percentage from 0 to {@link #maxAccuracy}; checked against a random 0-99 roll on
     * fulfillment.
     */
    private final int accuracy;

    /**
     * The human-readable prophecy message, e.g. "At sunset tomorrow, Fred will fall in to a deep sleep."
     */
    private final String prophecyMessage;

    /**
     * Whether this prophecy is expired; a killed prophecy is never fulfilled and is removed on the next upkeep.
     */
    private boolean kill = false;

    /**
     * Constructor
     *
     * @param plugin         a reference to the plugin
     * @param effectType     the effect applied if the prophecy succeeds
     * @param message        the human-readable prophecy message
     * @param targetID       the target player who will receive the effect
     * @param prophetID      the prophet who made the prophecy
     * @param delayTime      the number of game ticks until fulfillment
     * @param effectDuration the duration of the effect in game ticks
     * @param accuracy       the success probability; clamped to 0 - {@link #maxAccuracy}
     */
    public O2Prophecy(@NotNull Ollivanders2 plugin, @NotNull O2EffectType effectType, @NotNull String message, @NotNull UUID targetID, @NotNull UUID prophetID, long delayTime, int effectDuration, int accuracy) {
        p = plugin;
        this.effectType = effectType;
        this.targetID = targetID;
        this.prophetID = prophetID;
        this.prophecyMessage = message;
        this.time = delayTime;
        this.duration = effectDuration;

        if (accuracy > maxAccuracy)
            this.accuracy = maxAccuracy;
        else if (accuracy < 0)
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
     */
    public void age() {
        time = time - 1;
    }

    /**
     * Mark this prophecy as expired so it is never fulfilled and is removed on the next upkeep.
     */
    public void kill() {
        kill = true;
    }

    /**
     * Fulfill this prophecy: if the target is offline, stash it for retry when they rejoin; otherwise run the accuracy
     * check and, on success, apply the effect to the target and broadcast the outcome, or on failure privately tell the
     * prophet it did not come to pass. No-ops if already killed, and kills the prophecy once handled.
     */
    public void fulfill() {
        if (Ollivanders2.debug) {
            p.getLogger().info("Fulfilling prophecy");
        }

        if (kill) {
            return;
        }

        Player target = p.getServer().getPlayer(targetID);

        if (target == null) {
            Ollivanders2API.getProphecies().addOfflineProphecy(this);
            return;
        }

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        // maxSpellLevel under test mode forces success so probabilistic prophecy tests are deterministic
        if (accuracy > rand || (Ollivanders2.testMode && Ollivanders2.maxSpellLevel)) {
            O2Effect effect;
            Class<?> effectClass = effectType.getClassName();

            try {
                // effect constructor signature: (Ollivanders2, int duration, boolean isPermanent, UUID targetID)
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, boolean.class, UUID.class).newInstance(p, duration, false, targetID);
            }
            catch (Exception e) {
                e.printStackTrace();
                kill();
                return;
            }

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
