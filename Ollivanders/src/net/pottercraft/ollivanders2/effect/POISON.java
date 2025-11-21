package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Poison potion effect that causes periodic damage to the affected player.
 *
 * <p>POISON applies Minecraft's POISON potion effect to inflict periodic damage to the target
 * player. The poison damage diminishes the player's health over time but stops before killing them
 * (damage is reduced when the player reaches critical health). The effect is powered by the
 * Minecraft potion effect system with strength (amplifier) set to 1, determining the poison
 * intensity. The effect is detectable by both mind-reading spells (Legilimens) and information
 * spells (Informous) which report the target "feels sick".</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class POISON extends PotionEffectSuper {
    /**
     * Constructor for creating a poison potion effect.
     *
     * <p>Creates a potion effect that inflicts poison damage to the target player using Minecraft's
     * POISON potion effect type. Sets detection text for both mind-reading spells (Legilimens) and
     * information spells (Informous) to "feels sick".</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the poison effect in game ticks
     * @param pid      the unique ID of the player to poison
     */
    public POISON(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.POISON;
        potionEffectType = PotionEffectType.POISON;
        informousText = legilimensText = "feels sick";
    }

    /**
     * Perform cleanup when the poison effect is removed.
     *
     * <p>The default implementation does nothing, as POISON is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the poison damage stops and the player's health remains at its current value.</p>
     */
    @Override
    public void doRemove() {
    }
}