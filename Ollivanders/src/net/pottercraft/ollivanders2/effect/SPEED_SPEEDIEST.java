package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SPEED_SPEEDIEST extends PotionEffectSuper {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public SPEED_SPEEDIEST(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        strength = 4;

        effectType = O2EffectType.SPEED_SPEEDIEST;
        potionEffectType = PotionEffectType.SPEED;
        informousText = legilimensText = "is moving extremely fast";

        divinationText.add("will wear the boots of Mercury");
        divinationText.add("will move with the power of the gods");
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}