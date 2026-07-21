package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Applies Minecraft's hunger potion effect, accelerating the player's food depletion. Detectable via Informous and
 * Legilimens as "is hungry"; notifies the player "You feel hungry.".
 *
 * @author Azami7
 * @see PotionEffect
 */
public class HUNGER extends PotionEffect {
    /**
     * Constructor.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to afflict with hunger
     */
    public HUNGER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        strength = 1;

        effectType = O2EffectType.HUNGER;
        checkDurationBounds();

        informousText = legilimensText = "is hungry";

        potionEffectType = PotionEffectType.HUNGER;
        affectedPlayerText = "You feel hungry.";
    }

    @Override
    public void doRemove() {
    }
}