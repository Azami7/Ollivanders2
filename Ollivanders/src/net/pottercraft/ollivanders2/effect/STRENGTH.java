package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * STRENGTH effect - increases a player's melee damage.
 *
 * <p>When applied to a player, this effect increases their melee damage output, making all
 * physical attacks deal significantly more damage. It is a beneficial potion effect that provides
 * a temporary combat boost. The effect uses the Minecraft STRENGTH potion effect with strength
 * level 1.</p>
 *
 * <p>The effect displays "feels strong" in informous and legilimency spell text, indicating
 * the increased strength provided by this effect.</p>
 *
 * @author Azami7
 */
public class STRENGTH extends PotionEffectSuper {
    /**
     * Constructor for STRENGTH effect.
     *
     * <p>Initializes the effect with the specified duration and permanent flag. Sets up the effect
     * type as STRENGTH, applies strength level 1, and configures the informous and legilimency
     * spell text to "feels strong".</p>
     *
     * @param plugin the plugin instance
     * @param duration the duration of the effect in server ticks
     * @param isPermanent whether the effect should be permanent
     * @param pid the UUID of the player affected by this effect
     */
    public STRENGTH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.STRENGTH;
        checkDurationBounds();

        potionEffectType = PotionEffectType.STRENGTH;
        informousText = legilimensText = "feels strong";

        strength = 1;
    }

    /**
     * Clean up the STRENGTH effect when removed.
     *
     * <p>Called when the effect is removed from a player. This effect has no special cleanup
     * required beyond the standard effect removal handled by the parent class.</p>
     */
    @Override
    public void doRemove() {
    }
}
