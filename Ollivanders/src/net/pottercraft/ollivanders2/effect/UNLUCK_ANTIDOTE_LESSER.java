package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Weak antidote to the {@link UNLUCK} potion effect, reducing its remaining duration by 25% (strength 0.25).
 *
 * @author Azami7
 * @see PotionEffectAntidote
 * @see UNLUCK
 */
public class UNLUCK_ANTIDOTE_LESSER extends PotionEffectAntidote {
    /**
     * Constructor
     *
     * @param plugin      a reference to the plugin for logging
     * @param duration    ignored - antidotes apply immediately and are resolved
     * @param isPermanent ignored - antidotes are immediately applied and resolved
     * @param pid         the unique ID of the player to treat with the antidote
     */
    public UNLUCK_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.UNLUCK_ANTIDOTE_LESSER;
        checkDurationBounds();

        potionEffectType = PotionEffectType.UNLUCK;
        strength = 0.25;
    }

    @Override
    public void doRemove() {
    }
}
