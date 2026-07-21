package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.jetbrains.annotations.NotNull;

/**
 * Marker effect that suppresses the secondary symptoms of the {@link LYCANTHROPY} curse (its {@link AGGRESSION}
 * and {@link LYCANTHROPY_SPEECH} effects during full-moon transformation) without removing the curse itself. It
 * does this by setting the relief flag on the active LYCANTHROPY effect. Detectable via Informous.
 *
 * @author Azami7
 * @see LYCANTHROPY
 * @see AGGRESSION
 * @see LYCANTHROPY_SPEECH
 */
public class LYCANTHROPY_RELIEF extends O2Effect {
    /**
     * Constructor.
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
     * Age the effect and set the relief flag on the target's {@link LYCANTHROPY} effect. Kills this effect if the
     * target no longer has an active LYCANTHROPY curse to suppress.
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
     * Clear the relief flag on the target's {@link LYCANTHROPY} effect so its secondary effects resume at the next
     * transformation. No-op if the target no longer has the LYCANTHROPY effect.
     */
    @Override
    public void doRemove() {
        LYCANTHROPY lycanthropy = (LYCANTHROPY) Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY);
        if (lycanthropy != null) {
            lycanthropy.setRelief(false);
        }
    }
}