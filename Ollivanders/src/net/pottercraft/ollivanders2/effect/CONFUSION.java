package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Effect that applies the Minecraft nausea potion effect to simulate disorientation and confusion.
 *
 * <p>The CONFUSION effect applies Minecraft's native NAUSEA potion effect to the target player, causing
 * visual disorientation that simulates magical confusion. This effect inherits from PotionEffectSuper and
 * uses the randomized duration mechanism where the actual duration applied is calculated as: baseTime × random
 * multiplier (between 1-5).</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Applies Minecraft PotionEffectType.NAUSEA to the player</li>
 * <li>Strength set to 1 (full nausea effect amplifier)</li>
 * <li>Duration randomized: baseTime × (random 1-5 multiplier)</li>
 * <li>Detectable by both information spells (Informous) and mind-reading spells (Legilimens)</li>
 * <li>Detection text: "feels confused"</li>
 * </ul>
 *
 * @author Azami7
 * @see PotionEffectSuper for the potion effect application mechanism and randomized duration
 */
public class CONFUSION extends PotionEffectSuper {
    /**
     * Constructor for creating a confusion (nausea) potion effect.
     *
     * <p>Creates an effect that applies the Minecraft NAUSEA potion effect to the target player. The duration
     * parameter is used as the base time; the actual duration applied will be randomized by the parent class
     * (baseTime × random multiplier between 1-5). Sets strength to 1 for full nausea effect amplification and
     * detection text to "feels confused".</p>
     *
     * @param plugin      a callback to the plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the player to confuse
     */
    public CONFUSION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.CONFUSION;
        checkDurationBounds();

        potionEffectType = PotionEffectType.NAUSEA;
        informousText = legilimensText = "feels confused";

        strength = 1;
    }

    /**
     * Perform cleanup when the confusion effect is removed.
     *
     * <p>The default implementation does nothing, as potion effect removal is handled by the parent
     * class (PotionEffectSuper). When this effect is removed, the NAUSEA potion effect is
     * automatically stripped from the player by the parent class.</p>
     */
    @Override
    public void doRemove() {
    }
}