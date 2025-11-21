package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Luck potion effect that increases the player's item drop rates and loot quality.
 *
 * <p>LUCK applies Minecraft's LUCK potion effect to enhance the target player's fortune with
 * improved item drop rates and loot quality. The effect increases the likelihood of receiving
 * better loot from mobs, fishing, and mining while the effect is active. The effect is powered
 * by the Minecraft potion effect system with strength (amplifier) set to 1, determining the luck
 * magnitude. The effect is detectable by mind-reading spells (Legilimens) and information spells
 * (Informous) which report the target "feels lucky". The player receives an affectation
 * notification of "You feel lucky." when the effect is applied.</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class LUCK extends PotionEffectSuper {
    /**
     * Constructor for creating a luck potion effect.
     *
     * <p>Creates a potion effect that increases the target player's fortune with improved item drop
     * rates using Minecraft's LUCK potion effect type. The effect is detected by information spells
     * as the target "feels lucky" and notifies the player "You feel lucky."</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the luck effect in game ticks
     * @param pid      the unique ID of the player to bless with luck
     */
    public LUCK(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.LUCK;
        potionEffectType = PotionEffectType.LUCK;
        informousText = legilimensText = "feels lucky";
        affectedPlayerText = "You feel lucky.";
    }

    /**
     * Perform cleanup when the luck effect is removed.
     *
     * <p>The default implementation does nothing, as LUCK is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's item drop rates return to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}
