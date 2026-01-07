package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Parent class for antidotes that neutralize standard Minecraft potion effects.
 *
 * <p>PotionEffectAntidoteSuper provides a strength-based antidote mechanism that reduces the duration of active
 * potion effects rather than completely removing them. Antidotes are implemented as instant effects: they apply
 * immediately in checkEffect() and then kill themselves in the same tick.
 * </p>
 *
 * <p>Antidote Mechanism:
 * When an antidote activates, it locates any active potion effect of the target type on the player. If found:
 * <ul>
 * <li>The potion effect is removed entirely</li>
 * <li>If strength is less than 1.0, a new potion effect of the same type is re-applied with reduced duration</li>
 * <li>The reduced duration is calculated as: newDuration = originalDuration × strength</li>
 * <li>If strength is 1.0 or higher, the effect is completely removed (no re-application)</li>
 * </ul>
 * <p>Examples:</p>
 * <ul>
 * <li>strength = 1.0: Removes poison effect entirely (full antidote)</li>
 * <li>strength = 0.5: Re-applies poison with half the original duration (partial antidote)</li>
 * <li>strength = 0.25: Re-applies poison with 1/4 the original duration (weak antidote)</li>
 * </ul>
 *
 * <p>Effect Lifecycle:
 * Antidote effects are designed to be "instant" - they perform their action immediately when created and
 * expire in the same game tick. Subclasses set potionEffectType to specify which Minecraft potion effect
 * to target.</p>
 *
 * @author Azami7
 */
public abstract class PotionEffectAntidoteSuper extends O2Effect {
    /**
     * The type of Minecraft potion effect this antidote targets.
     *
     * <p>Subclasses override this field to specify which potion effect (POISON, HARM, WEAKNESS, etc.) this
     * antidote will neutralize. This value is checked against active potion effects on the target player.</p>
     */
    PotionEffectType potionEffectType = PotionEffectType.GLOWING;

    /**
     * The strength of this antidote, represented as a duration multiplier.
     *
     * <p>Strength determines how much the potion effect's duration is reduced when this antidote is applied.
     * The new duration is calculated as: newDuration = originalDuration × strength.
     * Values range from 0.0 to 1.0:
     * <ul>
     * <li>strength = 1.0: Full antidote - removes the effect completely</li>
     * <li>strength = 0.5: Partial antidote - effect re-applied with half duration</li>
     * <li>strength = 0.25: Weak antidote - effect re-applied with 1/4 duration</li>
     * <li>strength = 0.0: No antidote - effect re-applied with 0 duration (effectively removed)</li>
     * </ul>
     * </p>
     */
    double strength = 0.25;

    /**
     * Constructor for creating a potion effect antidote.
     *
     * <p>Creates an antidote effect that will apply immediately and expire in a single game tick.
     * Subclasses should initialize potionEffectType and strength fields before the effect is added
     * to a player's effect list.</p>
     *
     * @param plugin      a reference to the plugin for logging
     * @param duration    ignored - antidotes apply immediately and are resolved
     * @param isPermanent ignored - antidotes are immediately applied and resolved
     * @param pid         the unique ID of the target player to receive this antidote
     */
    public PotionEffectAntidoteSuper(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, false, pid);
    }

    /**
     * Apply this antidote immediately and expire.
     *
     * <p>This method executes in a single game tick and performs the following steps:</p>
     * <ol>
     * <li>Checks if the target has an active potion effect matching potionEffectType (target initialized in O2Effect constructor)</li>
     * <li>If found: removes the potion effect completely</li>
     * <li>If strength is less than 1.0: re-applies the same potion effect with reduced duration
     *     (newDuration = originalDuration × strength)</li>
     * <li>Kills the antidote effect so it doesn't persist</li>
     * </ol>
     *
     * <p>Example: An antidote with strength 0.5 applied to a player with a 10-second poison effect
     * will remove the poison and immediately re-apply it with a 5-second duration.</p>
     */
    @Override
    public void checkEffect() {
        PotionEffect targetEffect = target.getPotionEffect(potionEffectType);

        if (targetEffect != null) {
            target.removePotionEffect(potionEffectType);

            if (strength < 1) {
                int newDuration = (int) (targetEffect.getDuration() * strength);

                target.addPotionEffect(new PotionEffect(potionEffectType, newDuration, targetEffect.getAmplifier()));
            }
        }

        kill();
    }

    /**
     * Get the strength/potency of this antidote effect.
     *
     * <p>Returns a value between 0.0 and 1.0+ that determines how effectively this antidote
     * reduces the target potion effect's duration:</p>
     * <ul>
     * <li><strong>strength &ge; 1.0:</strong> Full-strength antidote - completely removes the target effect</li>
     * <li><strong>0.5 &le; strength &lt; 1.0:</strong> Medium-strength antidote - reduces target effect duration by 50-99%</li>
     * <li><strong>0.0 &lt; strength &lt; 0.5:</strong> Weak antidote - reduces target effect duration by less than 50%</li>
     * </ul>
     *
     * @return the antidote strength as a decimal value (0.0 to 1.0+)
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Potion antidote effects cannot ever be permanent
     *
     * @param perm ignored - potion effects are always temporary
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
