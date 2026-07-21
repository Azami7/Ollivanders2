package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Reduced-potency antidote to the {@link HARM} effect, cancelling it at strength 0.25.
 *
 * @author Azami7
 * @see PotionEffectAntidote
 */
public class HARM_ANTIDOTE_LESSER extends PotionEffectAntidote {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - antidotes apply immediately and are resolved
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to treat with the antidote
     */
    public HARM_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.HARM_ANTIDOTE_LESSER;
        checkDurationBounds();

        potionEffectType = PotionEffectType.INSTANT_DAMAGE;
        strength = 0.25;
    }

    @Override
    public void doRemove() {
    }
}
