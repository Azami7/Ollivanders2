package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Make a player slow
 *
 * @author Azami7
 * @since 2.2.9
 */
public class SLOWNESS extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public SLOWNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.SLOWNESS;
        potionEffectType = PotionEffectType.SLOWNESS;
        informousText = legilimensText = "feels sluggish";
        affectedPlayerText = "You feel sluggish.";
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}