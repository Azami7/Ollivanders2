package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Speech suppression effect that prevents the affected player from chatting.
 *
 * <p>MUTED_SPEECH is a debilitating effect that silences the target player by preventing them from
 * sending any chat messages. All chat events from the muted player are cancelled, preventing their
 * messages from being delivered to the server or other players. The effect is detectable by both
 * mind-reading spells (Legilimens) and information spells (Informous) which report the target
 * "is unable to speak". The player receives an affectation notification of "You feel tongue-tied."
 * when the effect is applied.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Blocks all player chat messages through AsyncPlayerChatEvent cancellation</li>
 * <li>Player cannot communicate via chat while muted</li>
 * <li>Detectable by both mind-reading spells (Legilimens) and information spells (Informous)</li>
 * <li>Detection text: "is unable to speak"</li>
 * <li>Player affectation text: "You feel tongue-tied."</li>
 * <li>Effect expires naturally when duration reaches zero</li>
 * </ul>
 *
 * @author Azami7
 */
public class MUTED_SPEECH extends O2Effect {
    /**
     * Constructor for creating a muted speech effect.
     *
     * <p>Creates an effect that prevents the target player from sending any chat messages. Sets detection
     * text for both mind-reading spells (Legilimens) and information spells (Informous) to "is unable to
     * speak", and notifies the player "You feel tongue-tied." when the effect is applied.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the muted speech effect in game ticks
     * @param pid      the unique ID of the player to mute
     */
    public MUTED_SPEECH(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.MUTED_SPEECH;
        informousText = legilimensText = "is unable to speak";
        affectedPlayerText = "You feel tongue-tied.";
    }

    /**
     * Age the muted speech effect each game tick.
     *
     * <p>Called each game tick. This effect tracks its remaining duration. Chat message blocking is
     * handled through event cancellation in the doOnAsyncPlayerChatEvent() method. When the duration
     * reaches zero, the effect is automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the muted speech effect is removed.
     *
     * <p>The default implementation does nothing, as the muted speech effect has no persistent state to
     * clean up. When removed, the player regains normal chat ability.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Block all chat messages from the muted player.
     *
     * <p>Cancels the AsyncPlayerChatEvent to prevent the muted player's chat messages from being sent.
     * This ensures complete communication suppression for the duration of the effect.</p>
     *
     * @param event the player chat event to cancel
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("MUTED_SPEECH: cancelling AsyncPlayerChatEvent", null, null, false);
    }
}