package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's STRENGTH potion effect, boosting the target's melee damage. Detectable via Informous
 * and Legilimens.
 *
 * @author Azami7
 * @see PotionEffect
 */
public class STRENGTH extends PotionEffect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player affected by this effect
     */
    public STRENGTH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.STRENGTH;
        checkDurationBounds();

        potionEffectType = PotionEffectType.STRENGTH;
        informousText = legilimensText = "feels strong";

        strength = 1;
    }

    @Override
    public void doRemove() {
    }
}
