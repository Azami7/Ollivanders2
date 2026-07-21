package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Silences the target by cancelling all of their chat messages for the duration. Detectable via Informous and
 * Legilimens.
 *
 * @author Azami7
 */
public class MUTED_SPEECH extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the muted speech effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to mute
     */
    public MUTED_SPEECH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.MUTED_SPEECH;
        checkDurationBounds();

        informousText = legilimensText = "is unable to speak";
        affectedPlayerText = "You feel tongue-tied.";
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    @Override
    public void doRemove() {
    }

    /**
     * Cancel the chat event if it came from the muted player.
     *
     * @param event the player chat event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("MUTED_SPEECH: cancelling AsyncPlayerChatEvent", null, null, false);
    }
}