package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Speech replacement effect that makes the sleeping player produce sleep-related sounds instead of speaking.
 *
 * <p>SLEEP_SPEECH is a permanent debilitating effect that replaces the target player's chat messages
 * with sleep-related vocalizations. Instead of speaking normally, the affected player produces a
 * limited dictionary of sleeping sounds (snoring, mumbling, sleep murmurs) for communication. Each
 * chat message is replaced with a single randomly-selected sleep sound from the custom dictionary.
 * The effect extends the BABBLING mechanism to provide specialized speech replacement for sleeping
 * players. This is typically applied as a secondary symptom during sleep or unconsciousness effects.</p>
 *
 * <p>Speech Replacement Configuration:</p>
 * <ul>
 * <li>Custom sleep sound dictionary: snoring, mumbling, incoherent mumbling, varied sleep sounds</li>
 * <li>Dictionary size: 4 unique sleep vocalizations (all in italic formatting)</li>
 * <li>Maximum words per message: 1 (only one sound produced per chat attempt)</li>
 * <li>Permanent effect: true (duration-independent, applies immediately)</li>
 * <li>Effect type: SLEEP_SPEECH</li>
 * </ul>
 *
 * @author Azami7
 * @see BABBLING for the speech replacement mechanism
 */
public class SLEEP_SPEECH extends BABBLING {
    /**
     * Constructor for creating a sleep speech effect.
     *
     * <p>Creates a speech replacement effect that makes the sleeping player produce sleep-related sounds
     * instead of normal chat messages. Sets up the custom sleep sound dictionary with snoring, mumbling,
     * and sleep vocalizations. The effect is permanent and applies immediately when created.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration parameter (unused for permanent effects)
     * @param isPermanent ignored - sleep speech is always permanent
     * @param pid         the unique ID of the sleeping player to affect
     */
    public SLEEP_SPEECH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.SLEEP_SPEECH;

        dictionary = new ArrayList<>() {{
            add("§ozzzzzzzz");
            add("§osnore");
            add("§oincoherent mumbling");
            add("§ozzzz zzz zzzzzz");
        }};

        maxWords = 1;
    }

    /**
     * Perform cleanup when the sleep speech effect is removed.
     *
     * <p>The default implementation does nothing, as the sleep speech effect has no persistent state to
     * clean up. When removed, the player regains normal speech ability.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * always permanent
     * @param perm ignored - this is always true
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
