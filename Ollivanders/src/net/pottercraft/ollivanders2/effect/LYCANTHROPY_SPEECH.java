package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A permanent {@link BABBLING} variant that replaces the player's speech with wolf and dog vocalizations (howl,
 * bark, growl, snarl), applied as a secondary symptom of werewolf transformation.
 *
 * @author Azami7
 * @see BABBLING
 * @see LYCANTHROPY
 */
public class LYCANTHROPY_SPEECH extends BABBLING {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - lycanthropy speech is always permanent
     * @param isPermanent ignored - lycanthropy speech is always permanent
     * @param pid         the unique ID of the player to afflict with wolf-like speech
     */
    public LYCANTHROPY_SPEECH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.LYCANTHROPY_SPEECH;
        checkDurationBounds();

        dictionary = new ArrayList<>() {{
            add("§oHOOOOOOWLLLLLL");
            add("§obark bark bark bark");
            add("§ogrowl");
            add("§osnarl");
        }};

        maxWords = 3;
    }

    @Override
    public void doRemove() {
    }

    /**
     * No-op; this effect is always permanent.
     *
     * @param perm ignored - effect is always permanent
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}