package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Partial antidote that weakly counteracts the AWAKE effect by reducing its duration.
 *
 * <p>AWAKE_ANTIDOTE_LESSER is a reduced-potency antidote to the {@link AWAKE} effect. As an antidote,
 * it partially removes the awake state by reducing the remaining duration of any active AWAKE effect
 * on the target player. With a strength of 0.25 (25%), this antidote is the weakest counter to the
 * AWAKE effect, removing only 25% of the remaining duration.</p>
 *
 * <p>Antidote Mechanism (inherited from O2EffectAntidoteSuper):</p>
 * <ul>
 * <li>Targets O2EffectType.AWAKE specifically</li>
 * <li>Strength value 0.25 acts as a multiplier on the target effect's remaining duration</li>
 * <li>Duration reduced = AWAKE remaining duration × 0.25 (25% of remaining time removed)</li>
 * <li>If no AWAKE effect exists on the target, the antidote has no effect</li>
 * <li>Immediately removes the antidote effect itself after processing</li>
 * </ul>
 *
 * @author Azami7
 * @see O2EffectAntidoteSuper for the antidote mechanism and strength-based duration reduction
 */
public class AWAKE_ANTIDOTE_LESSER extends O2EffectAntidoteSuper {
    /**
     * Constructor for creating a weak AWAKE antidote effect.
     *
     * <p>Creates an antidote that targets the AWAKE effect with reduced potency (strength 0.25).
     * The duration parameter is accepted for API consistency with other effects but is not used
     * for antidote processing. The antidote immediately processes and removes itself after applying
     * its effect to any active AWAKE effect on the target.</p>
     *
     * @param plugin   a reference to the plugin for logging
     * @param duration ignored - antidotes apply immediately and do not persist
     * @param pid      the unique ID of the target player
     */
    public AWAKE_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.AWAKE_ANTIDOTE_LESSER;
        o2EffectType = O2EffectType.AWAKE;
        strength = 0.25;
    }

    /**
     * Perform cleanup when the antidote effect is removed.
     *
     * <p>The default implementation does nothing, as antidote effects have no state to clean up.
     * The antidote's work is complete once it has reduced the target effect's duration.</p>
     */
    @Override
    public void doRemove() {
    }
}
