package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Make a player unlucky
 *
 * @author Azami7
 * @since 2.2.9
 */
public class UNLUCK extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public UNLUCK(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.UNLUCK;
        potionEffectType = PotionEffectType.UNLUCK;
        informousText = legilimensText = "feels unlucky";
        affectedPlayerText = "You feel unlucky.";

        divinationText.add("will be cursed by misfortune");
        divinationText.add("shall be cursed");
        divinationText.add("will find nothing but misfortune");
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}
