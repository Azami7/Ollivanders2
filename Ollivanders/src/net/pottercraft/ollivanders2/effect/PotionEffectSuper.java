package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Parent class for effects that apply Minecraft potion effects to players.
 *
 * <p>PotionEffectSuper provides a potion effect application mechanism that applies effects
 * immediately and then expires. Potion effects are applied in checkEffect() and the effect
 * kills itself in the same tick.
 * </p>
 *
 * <p>Duration Mechanism:</p>
 * Potion effects are applied with a duration clamped to a valid range:
 * <ul>
 * <li>Minimum duration: 2400 ticks (2 minutes)</li>
 * <li>Maximum duration: 6000 ticks (5 minutes)</li>
 * <li>The duration specified in the constructor is clamped to this range</li>
 * </ul>
 *
 * <p>Effect Configuration:
 * Subclasses set potionEffectType to specify which Minecraft potion effect (POISON, WEAKNESS, etc.)
 * to apply, and strength to set the amplifier level of the effect (1 is normal strength).</p>
 *
 * @author Azami7
 */
public abstract class PotionEffectSuper extends O2Effect {
    /**
     * The amplifier level of this potion effect.
     *
     * <p>Strength represents the amplifier level for the Minecraft potion effect. A value of 1 is
     * normal strength. Higher values increase the effect's potency:
     * <ul>
     * <li>strength = 1: Normal potency (amplifier 1)</li>
     * <li>strength = 2: Enhanced potency (amplifier 2)</li>
     * <li>strength = 3: Stronger potency (amplifier 3)</li>
     * </ul>
     * Subclasses typically set this value before the effect is applied.</p>
     */
    int strength = 1;

    /**
     * Maximum duration for a potion effect
     */
    public static int maxDuration = 6000; // 5 minutes

    /**
     * The type of Minecraft potion effect to apply.
     *
     * <p>Subclasses override this field to specify which potion effect (POISON, WEAKNESS, GLOWING, etc.)
     * will be applied to the target player. This value is used when creating the potion effect in checkEffect().</p>
     */
    PotionEffectType potionEffectType = PotionEffectType.GLOWING;

    /**
     * Constructor for creating a potion effect.
     *
     * <p>Creates an effect that will apply a Minecraft potion effect immediately and expire in a single
     * game tick. Subclasses should initialize potionEffectType and strength fields before the effect
     * is added to a player's effect list.</p>
     *
     * @param plugin      a reference to the plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the target player
     */
    public PotionEffectSuper(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, false, pid);

        minDuration = 2400; // minDuration for a potion effect is 2 minutes

        // make sure duration is between the min and max allowed
        this.duration = duration;
        if (this.duration < minDuration)
            this.duration = minDuration;
        else if (this.duration > maxDuration)
            this.duration = maxDuration;
    }

    /**
     * Apply the potion effect immediately and expire.
     *
     * <p>This method executes in a single game tick and performs the following:</p>
     * <ol>
     * <li>Locates the target player on the server</li>
     * <li>Creates a new potion effect with the specified type, duration, and strength (amplifier)</li>
     * <li>Applies the potion effect to the target</li>
     * <li>Kills the effect so it doesn't persist</li>
     * </ol>
     *
     * <p>Example: A POISON effect with strength 2 will create a poison effect with amplifier 2 and the
     * duration specified when the effect was created (clamped to 2400-6000 ticks).</p>
     */
    @Override
    public void checkEffect() {
        Player target = p.getServer().getPlayer(targetID);

        if (target != null) {
            target.addPotionEffect(new PotionEffect(potionEffectType, duration, strength));
        }

        kill();
    }

    /**
     * Set the amplifier strength of this potion effect.
     *
     * <p>Sets the strength (amplifier level) of the potion effect that will be applied to the player.
     * A value of 1 represents normal strength. Higher values increase the effect's potency.</p>
     *
     * @param s a positive integer representing the amplifier level (1 = normal, 2 = enhanced, etc.)
     */
    public void setStrength(int s) {
        strength = s;
    }

    /**
     * Get the strength for this potion effect
     *
     * @return the potion strength
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Get the PotionEffectType this PotionEffect effect adds
     *
     * @return the PotionEffectType
     */
    public PotionEffectType getPotionEffectType() {
        return potionEffectType;
    }

    /**
     * Potion effects cannot ever be permanent
     *
     * @param perm ignored - potion effects are always temporary
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
