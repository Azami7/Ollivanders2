package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Size increase effect that enlarges the affected player to double their normal scale.
 *
 * <p>SWELLING is a debilitating size-alteration effect that increases the target player's scale to
 * 200% of their normal size (scaleMultiplier = 2.0). The affected player appears physically larger
 * in the game world and has an expanded hitbox. The effect is detectable by both mind-reading spells
 * (Legilimens) and information spells (Informous) which report the target "is unnaturally large".</p>
 *
 * <p>Size Adjustment:</p>
 * <ul>
 * <li>Scale multiplier: 2.0 (double normal size)</li>
 * <li>Effect applies immediately upon casting</li>
 * <li>Hitbox scaling: scales proportionally with player size</li>
 * <li>Detection text: "is unnaturally large"</li>
 * </ul>
 *
 * @author Azami7
 * @see PlayerChangeSizeSuper for the size adjustment mechanism
 */
public class SWELLING extends PlayerChangeSizeSuper {
    /**
     * Constructor for creating a size increase effect.
     *
     * <p>Creates a swelling effect that increases the target player's scale to double their normal size.
     * The effect applies immediately, causing the player to appear physically larger in the game world
     * with a correspondingly expanded hitbox.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the swelling effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to enlarge
     */
    public SWELLING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SWELLING;
        scaleMultiplier = 2; // increase the player's by double

        informousText = "is unnaturally large";

        startEffect();
    }
}
