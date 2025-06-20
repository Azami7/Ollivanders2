package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Sleep speech makes the player say "sleeping" sounds rather than talk.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class SLEEP_SPEECH extends BABBLING {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public SLEEP_SPEECH(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.SLEEP_SPEECH;

        dictionary = new ArrayList<>() {{
            add("§ozzzzzzzz");
            add("§osnore");
            add("§oincoherent mumbling");
            add("§ozzzz zzz zzzzzz");
        }};

        permanent = true;
        maxWords = 1;
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}
