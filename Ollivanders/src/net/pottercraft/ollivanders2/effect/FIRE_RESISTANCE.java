package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's fire resistance potion effect, making the player immune to fire and lava damage. Detectable via
 * Informous and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class FIRE_RESISTANCE extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to make fire-resistant
     */
    public FIRE_RESISTANCE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FIRE_RESISTANCE;
        checkDurationBounds();

        potionEffectType = PotionEffectType.FIRE_RESISTANCE;
        informousText = legilimensText = "does not fear fire";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}
