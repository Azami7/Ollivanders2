package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
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
     * Age the lycanthropy relief marker effect and set relief flag on lycanthropy.
     *
     * <p>Called each game tick. This method ages the effect by 1 tick and performs two key functions:</p>
     * <ol>
     * <li>Checks if the player still has the LYCANTHROPY effect. If not found, kills this relief effect
     * since it cannot function without an active lycanthropy curse to suppress.</li>
     * <li>Sets the relief flag on the LYCANTHROPY effect to true, signaling that secondary effects
     * (AGGRESSION and LYCANTHROPY_SPEECH) should be suppressed during transformation. The LYCANTHROPY
     * effect checks this flag during checkEffect() to prevent applying secondary effects.</li>
     * </ol>
     *
     * <p>When the duration reaches zero, the effect is automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);

        LYCANTHROPY lycanthropy = (LYCANTHROPY) Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY);
        if (lycanthropy == null) {
            kill();
            return;
        }

        if (!lycanthropy.getRelief())
            lycanthropy.setRelief(true);
    }

    /**
     * Perform cleanup when the lycanthropy relief effect is removed.
     *
     * <p>When the relief effect is removed (either by duration expiration or manual removal),
     * this method clears the relief flag on the LYCANTHROPY effect. This allows the lycanthropy
     * curse to resume applying its secondary effects (AGGRESSION and LYCANTHROPY_SPEECH) during
     * future transformations at moonrise. If the player no longer has the LYCANTHROPY effect,
     * no cleanup is needed.</p>
     */
    @Override
    public void doRemove() {
        LYCANTHROPY lycanthropy = (LYCANTHROPY) Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY);
        if (lycanthropy != null) {
            lycanthropy.setRelief(false);
        }
    }
}