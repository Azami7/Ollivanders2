package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Hunger potion effect that increases the target player's hunger and food depletion.
 *
 * <p>HUNGER applies Minecraft's HUNGER potion effect to increase the target player's hunger
 * level and cause them to deplete food faster while active. The effect is powered by the
 * Minecraft potion effect system with strength (amplifier) set to 1, determining the hunger
 * intensity. The effect is detectable by mind-reading spells (Legilimens) and information
 * spells (Informous) which report the target "is hungry". The player receives an affectation
 * notification of "You feel hungry." when the effect is applied.</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class HUNGER extends PotionEffectSuper {
    /**
     * Constructor for creating a hunger potion effect.
     *
     * <p>Creates a potion effect that causes hunger and accelerated food depletion to the target
     * player using Minecraft's HUNGER potion effect type. The effect is detected by information
     * spells as the target "is hungry" and notifies the player "You feel hungry."</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration in ticks, snapped to min of 2 minutes, max of 10 minutes
     * @param pid      the unique ID of the player to afflict with hunger
     */
    public HUNGER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.HUNGER;
        potionEffectType = PotionEffectType.HUNGER;
        informousText = legilimensText = "is hungry";
        affectedPlayerText = "You feel hungry.";
    }

    /**
     * Perform cleanup when the hunger effect is removed.
     *
     * <p>The default implementation does nothing, as HUNGER is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's hunger depletion rate returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}