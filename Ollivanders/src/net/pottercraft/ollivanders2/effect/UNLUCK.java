package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's UNLUCK potion effect (amplifier 1), lowering the player's luck and loot quality. Detectable via
 * Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect for the potion effect application mechanism
 */
public class UNLUCK extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, clamped to min 2 minutes, max 10 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to curse with unluck
     */
    public UNLUCK(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.UNLUCK;
        checkDurationBounds();

        potionEffectType = PotionEffectType.UNLUCK;

        informousText = legilimensText = "feels unlucky";
        affectedPlayerText = "You feel unlucky.";
    }

    @Override
    public void doRemove() {
    }
}
