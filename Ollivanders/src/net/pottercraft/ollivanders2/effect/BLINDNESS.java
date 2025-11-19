package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Make a player blind
 *
 * @author Azami7
 * @since 2.2.9
 */
public class BLINDNESS extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param duration the duration for the effect
     * @param pid      the pid of the affected player
     */
    public BLINDNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.BLINDNESS;
        potionEffectType = PotionEffectType.BLINDNESS;
        informousText = legilimensText = "cannot see";

        strength = 1;
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}