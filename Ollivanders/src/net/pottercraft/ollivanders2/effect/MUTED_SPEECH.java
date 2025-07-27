package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Mute player's speech
 */
public class MUTED_SPEECH extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public MUTED_SPEECH(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.MUTED_SPEECH;
        informousText = legilimensText = "is unable to speak";
        affectedPlayerText = "You feel tongue-tied.";

        divinationText.add("will be struck mute");
        divinationText.add("shall lose their mind to insanity");
        divinationText.add("shall be afflicted in the mind");
        divinationText.add("will fall silent");
    }

    /**
     * Age the effect by 1 every game tick.
     */
    public void checkEffect() {
        age(1);
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }

    /**
     * Do any on player chat effects
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("MUTED_SPEECH: cancelling AsyncPlayerChatEvent", null, null, false);
    }
}