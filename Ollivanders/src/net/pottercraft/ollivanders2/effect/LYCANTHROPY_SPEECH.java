package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Wolf-like speech effect that forces a player to vocalize dog and wolf sounds.
 *
 * <p>LYCANTHROPY_SPEECH is a specialized variant of the {@link BABBLING} effect that forces the
 * affected player to make dog and wolf sounds instead of speaking normal speech. Like BABBLING, this
 * effect applies periodic damage and forces the player to speak messages at random intervals. However,
 * LYCANTHROPY_SPEECH uses a custom dictionary of wolf/dog vocalizations (howls, barks, growls, snarls
 * with italic formatting) and is configured as a permanent effect with a maximum of 3 sound messages per
 * damage cycle. This effect is typically applied as a secondary symptom during werewolf transformation
 * when the LYCANTHROPY curse is active.</p>
 *
 * <p>Mechanism (inherited from BABBLING):</p>
 * <ul>
 * <li>Periodic damage applied every 3 seconds with clamped magnitude (0.5-10 health)</li>
 * <li>Forced speech output at random intervals with up to 3 wolf/dog sound messages per damage cycle</li>
 * <li>Custom dog sound dictionary with 4 vocalizations: howl, bark, growl, snarl</li>
 * <li>Permanent effect while active (applied during werewolf transformation)</li>
 * <li>All speech output formatted with italic styling</li>
 * </ul>
 *
 * @author Azami7
 * @see BABBLING for the base periodic speech and damage mechanism
 * @see LYCANTHROPY for the curse that applies this effect as a secondary symptom
 */
public class LYCANTHROPY_SPEECH extends BABBLING {
    /**
     * Constructor for creating a wolf-like lycanthropy speech effect.
     *
     * <p>Creates an effect that forces the target player to make wolf and dog sounds. Extends the parent
     * BABBLING class with a custom sound dictionary (4 dog vocalizations with italic formatting) and is
     * configured as permanent with a maximum of 3 sounds per speech cycle. Inherits all damage mechanics
     * from BABBLING: periodic damage every 3 seconds with clamped 0.5-10 damage range. This effect is
     * typically applied as a secondary symptom of werewolf transformation during LYCANTHROPY curse
     * activation.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the speech effect in game ticks
     * @param pid      the unique ID of the player to afflict with wolf-like speech
     */
    public LYCANTHROPY_SPEECH(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.LYCANTHROPY_SPEECH;

        dictionary = new ArrayList<>() {{
            add("§oHOOOOOOWLLLLLL");
            add("§obark bark bark bark");
            add("§ogrowl");
            add("§osnarl");
        }};

        permanent = true;
        maxWords = 3;
    }

    /**
     * Perform cleanup when the lycanthropy speech effect is removed.
     *
     * <p>The default implementation does nothing, as the lycanthropy speech effect has no persistent state
     * to clean up. When removed, the player regains normal speech ability.</p>
     */
    @Override
    public void doRemove() {
    }
}