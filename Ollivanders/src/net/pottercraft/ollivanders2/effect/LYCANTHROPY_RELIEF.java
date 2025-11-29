package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker effect that suppresses the secondary effects of lycanthropy curse.
 *
 * <p>LYCANTHROPY_RELIEF is a temporary marker effect that suppresses the unwanted symptoms of
 * the LYCANTHROPY curse. When applied to a player affected by lycanthropy, this effect does not
 * directly remove the curse itself, but rather suppresses the secondary effects (AGGRESSION and
 * LYCANTHROPY_SPEECH) that normally accompany werewolf transformation during full moons. The effect
 * is passive and only needs to be tracked until expiration. The effect is detectable by information
 * spells (Informous) which report the target "looks unwell".</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Passive marker effect - no active behavior during each tick</li>
 * <li>Suppresses AGGRESSION and LYCANTHROPY_SPEECH secondary effects from lycanthropy curse</li>
 * <li>Does not remove the permanent LYCANTHROPY curse itself</li>
 * <li>Detectable by information spells (Informous)</li>
 * <li>Detection text: "looks unwell"</li>
 * <li>Effect expires naturally when duration reaches zero</li>
 * </ul>
 *
 * @author Azami7
 * @see LYCANTHROPY for the lycanthropy curse being suppressed
 * @see AGGRESSION for the aggressive behavior effect suppressed by this relief
 * @see LYCANTHROPY_SPEECH for the speech effect suppressed by this relief
 */
public class LYCANTHROPY_RELIEF extends O2Effect {
    /**
     * Constructor for creating a lycanthropy relief passive marker effect.
     *
     * <p>Creates a temporary marker effect that suppresses the secondary symptoms of lycanthropy.
     * This effect is passive and does not directly remove the curse; the LYCANTHROPY effect itself
     * determines whether to apply AGGRESSION and LYCANTHROPY_SPEECH based on whether this relief
     * effect is present. Sets the detection text for information spells to "looks unwell".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the relief effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to provide lycanthropy relief
     */
    public LYCANTHROPY_RELIEF(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.LYCANTHROPY_RELIEF;
        checkDurationBounds();

        informousText = "looks unwell";
    }

    /**
     * Age the lycanthropy relief marker effect each game tick.
     *
     * <p>Called each game tick. This effect is passive and only needs to track its remaining duration.
     * The actual suppression of secondary effects is handled by the LYCANTHROPY effect when it checks
     * for this effect's presence during transformation. When the duration reaches zero, the effect is
     * automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the lycanthropy relief effect is removed.
     *
     * <p>The default implementation does nothing, as the lycanthropy relief effect is a passive marker
     * with no state to clean up. When removed, the player's secondary lycanthropy symptoms will resume
     * on the next full moon transformation if the underlying LYCANTHROPY curse is still active.</p>
     */
    @Override
    public void doRemove() {
    }
}