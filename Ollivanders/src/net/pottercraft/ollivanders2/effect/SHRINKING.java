package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Size reduction effect that shrinks the affected player to half their normal scale.
 *
 * <p>SHRINKING is a debilitating size-alteration effect that reduces the target player's scale to
 * 50% of their normal size (scaleMultiplier = 0.5). The affected player appears physically smaller
 * in the game world and is subject to reduced hitbox dimensions. The effect is detectable by both
 * mind-reading spells (Legilimens) and information spells (Informous) which report the target
 * "is unnaturally small".</p>
 *
 * <p>Size Adjustment:</p>
 * <ul>
 * <li>Scale multiplier: 0.5 (half normal size)</li>
 * <li>Effect applies immediately upon casting</li>
 * <li>Hitbox scaling: scales proportionally with player size</li>
 * <li>Detection text: "is unnaturally small"</li>
 * </ul>
 *
 * @author Azami7
 * @see PlayerChangeSizeSuper for the size adjustment mechanism
 */
public class SHRINKING extends PlayerChangeSizeSuper {
    /**
     * Constructor for creating a size reduction effect.
     *
     * <p>Creates a shrinking effect that reduces the target player's scale to 50% of their normal
     * size. The effect applies immediately, causing the player to appear physically smaller in the
     * game world with a correspondingly reduced hitbox.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shrinking effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to shrink
     */
    public SHRINKING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SHRINKING;
        checkDurationBounds();

        informousText = "is unnaturally small";

        scaleMultiplier = 0.5; // makes the player half size
    }
}
