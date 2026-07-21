package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's LUCK potion effect, improving the target's loot rolls. Detectable via Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class LUCK extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, clamped to the effect type's min and max
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to bless with luck
     */
    public LUCK(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.LUCK;
        checkDurationBounds();

        potionEffectType = PotionEffectType.LUCK;

        informousText = legilimensText = "feels lucky";
        affectedPlayerText = "You feel lucky.";
    }

    @Override
    public void doRemove() {
    }
}
