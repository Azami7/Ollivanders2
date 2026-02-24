package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fire Resistance effect that grants immunity to fire damage.
 *
 * <p>Applies the Minecraft fire resistance potion effect, making the player immune to fire and lava damage.
 * This effect can be temporary or permanent depending on spell duration.</p>
 *
 * @author Azami7
 */
public class FIRE_RESISTANCE extends PotionEffectSuper {
    /**
     * Constructor for Fire Resistance effect.
     *
     * @param plugin the Ollivanders2 plugin
     * @param duration the duration in ticks
     * @param isPermanent whether the effect is permanent
     * @param pid the player UUID
     */
    public FIRE_RESISTANCE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FIRE_RESISTANCE;
        checkDurationBounds();

        potionEffectType = PotionEffectType.FIRE_RESISTANCE;
        informousText = legilimensText = "does not fear fire";

        strength = 1;
    }

    /**
     * Cleanup when the effect is removed.
     *
     * <p>Fire Resistance has no special cleanup needed beyond removing the potion effect.</p>
     */
    @Override
    public void doRemove() {
    }
}
