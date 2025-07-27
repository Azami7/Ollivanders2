package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Give a player hunger
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HUNGER extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public HUNGER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.HUNGER;
        potionEffectType = PotionEffectType.HUNGER;
        informousText = legilimensText = "is hungry";
        affectedPlayerText = "You feel hungry.";

        divinationText.add("shall be struck by a terrible affliction");
        divinationText.add("will starve");
        divinationText.add("shall be cursed");
        divinationText.add("will become insatiable");
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}