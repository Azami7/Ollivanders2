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
public class SLOWNESS extends PotionEffectSuper
{
    public SLOWNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
    {
        super(plugin, duration, pid);

        strength = 1;

        effectType = O2EffectType.SLOWNESS;
        potionEffectType = PotionEffectType.SLOW;
        informousText = legilimensText = "feels confused";

        divinationText.add("shall be cursed");
        divinationText.add("will be afflicted in the mind");
        divinationText.add("will be struck by a terrible affliction");
        divinationText.add("will suffer a mental breakdown");
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove () { }
}