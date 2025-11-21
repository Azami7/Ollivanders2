package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Effect that applies the Minecraft blindness potion effect, obscuring the player's vision.
 *
 * <p>The BLINDNESS effect applies Minecraft's native BLINDNESS potion effect to the target player,
 * simulating magical blinding that obscures their visual perception. This effect inherits from
 * PotionEffectSuper and uses the randomized duration mechanism where the actual duration applied
 * is calculated as: baseTime × random multiplier (between 1-5).</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Applies Minecraft PotionEffectType.BLINDNESS to the player</li>
 * <li>Strength set to 1 (full blindness amplifier)</li>
 * <li>Duration randomized: baseTime × (random 1-5 multiplier)</li>
 * <li>Detectable by both information spells (Informous) and mind-reading spells (Legilimens)</li>
 * <li>Detection text: "cannot see"</li>
 * </ul>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism and randomized duration
 */
public class BLINDNESS extends PotionEffectSuper {
    /**
     * Constructor for creating a blindness potion effect.
     *
     * <p>Creates an effect that applies the Minecraft BLINDNESS potion effect to the target player.
     * The duration parameter is used as the base time; the actual duration applied will be randomized
     * by the parent class (baseTime × random multiplier between 1-5). Sets strength to 1 for full
     * blindness amplification and detection text to "cannot see".</p>
     *
     * @param plugin   a callback to the plugin
     * @param duration the base duration for the effect in game ticks (will be randomized)
     * @param pid      the unique ID of the player to blind
     */
    public BLINDNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.BLINDNESS;
        potionEffectType = PotionEffectType.BLINDNESS;
        informousText = legilimensText = "cannot see";

        strength = 1;
    }

    /**
     * Perform cleanup when the blindness effect is removed.
     *
     * <p>The default implementation does nothing, as potion effect removal is handled by the parent
     * class (PotionEffectSuper). When this effect is removed, the BLINDNESS potion effect is
     * automatically stripped from the player by the parent class.</p>
     */
    @Override
    public void doRemove() {
    }
}