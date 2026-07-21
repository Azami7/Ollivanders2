package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Weak antidote to the {@link AWAKE} effect, reducing its remaining duration by 25% (strength 0.25).
 *
 * @author Azami7
 * @see O2EffectAntidote
 */
public class AWAKE_ANTIDOTE_LESSER extends O2EffectAntidote {
    /**
     * Constructor
     *
     * @param plugin      a reference to the plugin for logging
     * @param duration    ignored - antidotes apply immediately and do not persist
     * @param isPermanent ignored - antidotes are immediately applied and resolved
     * @param pid         the unique ID of the target player
     */
    public AWAKE_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.AWAKE_ANTIDOTE_LESSER;
        checkDurationBounds();

        o2EffectType = O2EffectType.AWAKE;
        strength = 0.25;
    }

    @Override
    public void doRemove() {
    }
}
