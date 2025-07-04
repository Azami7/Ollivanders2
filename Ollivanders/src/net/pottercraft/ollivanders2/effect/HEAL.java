package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Heal a player
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HEAL extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public HEAL(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.HEAL;
        potionEffectType = PotionEffectType.INSTANT_HEALTH;
        informousText = legilimensText = "feels healthy";

        divinationText.add("will feel rejuvenated");
        divinationText.add("will be blessed by fortune");
        divinationText.add("shall be blessed");
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}