package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Unluck potion effect that reduces the affected player's luck and loot quality.
 *
 * <p>UNLUCK applies Minecraft's UNLUCK potion effect to decrease the target player's fortune and luck.
 * The effect reduces item drop rates and lowers the quality of loot obtained from activities like
 * fishing, mining, and mob drops. The effect is powered by the Minecraft potion effect system with
 * strength (amplifier) set to 1. The effect is detectable by both mind-reading spells (Legilimens)
 * and information spells (Informous) which report the target "feels unlucky". The player receives a
 * notification of "You feel unlucky." when the effect is applied.</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism
 */
public class UNLUCK extends PotionEffectSuper {
    /**
     * Constructor for creating an unluck effect.
     *
     * <p>Creates a potion effect that reduces the target player's luck and loot quality using Minecraft's
     * UNLUCK potion effect type with strength 1. Sets detection text for both mind-reading spells
     * (Legilimens) and information spells (Informous) to "feels unlucky", and notifies the player
     * "You feel unlucky." when the effect is applied.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 10 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to curse with unluck
     */
    public UNLUCK(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.UNLUCK;
        potionEffectType = PotionEffectType.UNLUCK;
        informousText = legilimensText = "feels unlucky";
        affectedPlayerText = "You feel unlucky.";
    }

    /**
     * Perform cleanup when the unluck effect is removed.
     *
     * <p>The default implementation does nothing, as UNLUCK is a potion effect whose effects
     * are automatically managed by the Minecraft potion system. When the effect expires or is
     * manually removed, the player's luck returns to normal.</p>
     */
    @Override
    public void doRemove() {
    }
}
