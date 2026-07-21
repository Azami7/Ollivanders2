package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Partial antidote to {@link POISON}: at strength 0.25 it cuts the remaining duration of active POISON effects by 25%.
 *
 * @author Azami7
 * @see PotionEffectAntidote
 * @see POISON
 */
public class POISON_ANTIDOTE_LESSER extends PotionEffectAntidote {
    /**
     * Constructor
     *
     * @param plugin      a reference to the plugin for logging
     * @param duration    ignored - antidotes apply immediately and are resolved
     * @param isPermanent ignored - antidotes are immediately applied and resolved
     * @param pid         the unique ID of the player to treat with the antidote
     */
    public POISON_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.POISON_ANTIDOTE_LESSER;
        checkDurationBounds();

        potionEffectType = PotionEffectType.POISON;
        strength = 0.25;
    }

    @Override
    public void doRemove() {
    }
}
