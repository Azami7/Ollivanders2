package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's INSTANT_DAMAGE potion effect to harm the affected player. Detectable via Informous and
 * Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 * @see BURNING
 */
public class HARM extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, clamped to the effect type's min/max bounds
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to harm
     */
    public HARM(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.HARM;
        checkDurationBounds();

        informousText = legilimensText = "feels unwell";

        potionEffectType = PotionEffectType.INSTANT_DAMAGE;
        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}