package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * faster speed effect
 *
 * @see SPEED
 */
public class SPEED_SPEEDIER extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public SPEED_SPEEDIER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 2;

        effectType = O2EffectType.SPEED_SPEEDIER;
        potionEffectType = PotionEffectType.SPEED;
        informousText = legilimensText = "is moving very fast";
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}
