package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Minor antidote to unluck potion effect
 *
 * @author Azami7
 */
public class UNLUCK_ANTIDOTE_LESSER extends PotionEffectAntidoteSuper {
    /**
     * Constructor
     *
     * @param plugin   a reference to the plugin for logging
     * @param duration the duration of this effect - not used for this effect type
     * @param pid      the target player
     */
    public UNLUCK_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.UNLUCK_ANTIDOTE_LESSER;
        potionEffectType = PotionEffectType.UNLUCK;
        strength = 0.25;
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}
