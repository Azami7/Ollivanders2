package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Permanent {@link BABBLING} variant that replaces the sleeping player's chat with a single randomly chosen sleep
 * sound (snoring, mumbling) per message. Typically applied as a secondary symptom of a sleep effect.
 *
 * @author Azami7
 * @see BABBLING
 */
public class SLEEP_SPEECH extends BABBLING {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - sleep speech is always permanent
     * @param isPermanent ignored - sleep speech is always permanent
     * @param pid         the unique ID of the sleeping player to affect
     */
    public SLEEP_SPEECH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.SLEEP_SPEECH;
        checkDurationBounds();

        dictionary = new ArrayList<>() {{
            add("§ozzzzzzzz");
            add("§osnore");
            add("§oincoherent mumbling");
            add("§ozzzz zzz zzzzzz");
        }};

        maxWords = 1;
    }

    @Override
    public void doRemove() {
    }

    /**
     * No-op: sleep speech is always permanent.
     *
     * @param perm ignored
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
