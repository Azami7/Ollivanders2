package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * REGENERATION effect - restores a player's health over time.
 *
 * <p>When applied to a player, this effect continuously restores their health over the duration
 * of the effect. It is a beneficial potion effect that provides passive healing. The effect uses
 * the Minecraft REGENERATION potion effect with strength level 1.</p>
 *
 * <p>The effect displays "feels healthy" in informous and legilimency spell text, indicating
 * the restorative nature of the effect.</p>
 *
 * @author Azami7
 */
public class REGENERATION extends PotionEffectSuper {
    /**
     * Constructor for REGENERATION effect.
     *
     * <p>Initializes the effect with the specified duration and permanent flag. Sets up the effect
     * type as REGENERATION, applies strength level 1, and configures the informous and legilimency
     * spell text to "feels healthy".</p>
     *
     * @param plugin the plugin instance
     * @param duration the duration of the effect in server ticks
     * @param isPermanent whether the effect should be permanent
     * @param pid the UUID of the player affected by this effect
     */
    public REGENERATION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.REGENERATION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.REGENERATION;
        informousText = legilimensText = "feels healthy";

        strength = 1;
    }

    /**
     * Clean up the REGENERATION effect when removed.
     *
     * <p>Called when the effect is removed from a player. This effect has no special cleanup
     * required beyond the standard effect removal handled by the parent class.</p>
     */
    @Override
    public void doRemove() {
    }
}
