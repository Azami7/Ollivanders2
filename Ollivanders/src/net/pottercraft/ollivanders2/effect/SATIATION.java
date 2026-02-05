package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * SATIATION effect - keeps a player's hunger bar full.
 *
 * <p>When applied to a player, this effect fills and maintains the player's hunger bar (food level),
 * preventing starvation. It is a beneficial potion effect that provides continuous sustenance.
 * The effect uses the Minecraft SATURATION potion effect with strength level 1.</p>
 *
 * <p>The effect displays "feels full" in informous and legilimency spell text, indicating the
 * satiation provided by this effect.</p>
 *
 * @author Azami7
 */
public class SATIATION extends PotionEffectSuper {
    /**
     * Constructor for SATIATION effect.
     *
     * <p>Initializes the effect with the specified duration and permanent flag. Sets up the effect
     * type as SATIATION, applies strength level 1, and configures the informous and legilimency
     * spell text to "feels full".</p>
     *
     * @param plugin the plugin instance
     * @param duration the duration of the effect in server ticks
     * @param isPermanent whether the effect should be permanent
     * @param pid the UUID of the player affected by this effect
     */
    public SATIATION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SATIATION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.SATURATION;
        informousText = legilimensText = "feels full";

        strength = 1;
    }

    /**
     * Clean up the SATIATION effect when removed.
     *
     * <p>Called when the effect is removed from a player. This effect has no special cleanup
     * required beyond the standard effect removal handled by the parent class.</p>
     */
    @Override
    public void doRemove() {
    }
}
