package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Effect that applies periodic fire damage to a player with visual burning effects.
 *
 * <p>The BURNING effect inflicts continuous fire damage to the target player at regular intervals (every 3 seconds).
 * Damage is calculated from the effect's duration parameter (damage = duration / 100) and is clamped to a range
 * of 0.5 to 10 health points per tick. The effect applies smoke particle effects and fire hurt sounds with each
 * damage instance, and prevents the player from being killed by fire (ensures health stays above 1).</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Initial damage calculated as: duration / 100 (clamped to 0.5-10 range)</li>
 * <li>Damage applied every 3 seconds (damageFrequencyInSeconds)</li>
 * <li>Cooldown tracked per tick (damageCooldown decrement)</li>
 * <li>Death prevention: damage won't reduce health below 1</li>
 * <li>Visual effects: smoke particles and fire hurt sound each damage tick</li>
 * <li>Detectable by information and mind-reading spells</li>
 * <li>Damage can be adjusted dynamically with addDamage() and removeDamage()</li>
 * </ul>
 *
 * @author Azami7
 */
public class BURNING extends O2Effect {
    /**
     * The minimum damage per tick that the burning effect can inflict (0.5 health points).
     *
     * <p>Used to clamp the damage value via addDamage() to ensure the effect always deals
     * at least minimum damage to the player.</p>
     */
    private final double minDamage = 0.5;

    /**
     * The maximum damage per tick that the burning effect can inflict (10 health points).
     *
     * <p>Used to clamp the damage value via addDamage() to prevent excessive damage scaling.</p>
     */
    private final double maxDamage = 10;

    /**
     * The current damage per tick inflicted by this burning effect.
     *
     * <p>Initialized from duration / 100 in the constructor. Can be adjusted via addDamage()
     * and removeDamage() methods, but is always clamped to the range [minDamage, maxDamage].
     * Also stored in the duration field (damage × 100) for serialization purposes.</p>
     */
    private double damage;

    /**
     * The interval (in seconds) between damage application to the player.
     *
     * <p>Default value is 3 seconds. Each damage tick is preceded by a cooldown countdown
     * that measures damageFrequencyInSeconds × ticksPerSecond ticks.</p>
     */
    private int damageFrequencyInSeconds = 3;

    /**
     * Tick counter for the damage application cooldown.
     *
     * <p>Counts down from damageFrequencyInSeconds × ticksPerSecond (60 ticks for default 3 seconds).
     * When this reaches 0 or below, damage is applied and the counter is reset. Decrements by 1 each tick.</p>
     */
    private int damageCooldown = 0;

    /**
     * Constructor for creating a burning fire damage effect.
     *
     * <p>Creates an effect that applies periodic fire damage to the target player. Initial damage is calculated
     * from the duration parameter using the formula: damage = duration / 100. The calculated damage is not clamped
     * at this point but will be clamped to [0.5, 10] range when adjusted via addDamage(). Detection text is set to
     * "is afflicted with a terrible burning" for both information and mind-reading spells.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks (used to calculate initial damage as duration / 100)
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to burn
     */
    public BURNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.BURNING;
        checkDurationBounds();

        informousText = legilimensText = "is afflicted with a terrible burning";

        damage = (double) duration / 100;
    }

    /**
     * Age the effect and apply periodic damage based on cooldown countdown.
     *
     * <p>Called each game tick. This method:</p>
     * <ol>
     * <li>Ages the effect (decrements duration until expiration)</li>
     * <li>Checks if the target player still exists; kills effect if not</li>
     * <li>Checks if damageCooldown has reached 0 or below</li>
     * <li>If cooldown reached: applies damage via doDamage() and resets damageCooldown</li>
     * <li>If cooldown not reached: decrements damageCooldown by 1</li>
     * </ol>
     */
    @Override
    public void checkEffect() {
        age(1);

        Player target = p.getServer().getPlayer(targetID);

        if (target == null) {
            kill();
            return;
        }

        if (damageCooldown <= 0) {
            doDamage();
            damageCooldown = damageFrequencyInSeconds * Ollivanders2Common.ticksPerSecond;
        }
        else
            damageCooldown = damageCooldown - 1;
    }

    /**
     * Apply damage to the player with visual burning effects.
     *
     * <p>Applies the current damage amount to the target player, preventing death (health won't drop below 1).
     * On successful damage, plays visual and audio effects:
     * <ul>
     * <li>Smoke particle effect (Effect.SMOKE with intensity 4) at player location</li>
     * <li>Fire hurt sound (Sound.ENTITY_PLAYER_HURT_ON_FIRE) at player location</li>
     * </ul>
     * If the player is not found, kills the effect.</p>
     */
    private void doDamage() {
        Player target = p.getServer().getPlayer(targetID);
        if (target != null) {
            if ((target.getHealth() - damage) > 1)
                target.damage(damage);

            target.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 4);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, 0);
        }
        else
            kill();
    }

    /**
     * Adjust the damage done by this burning effect with clamping to bounds.
     *
     * <p>Increases (or decreases, if d is negative) the current damage value, then clamps it to the valid
     * range [minDamage, maxDamage] (0.5 to 10). The damage value is also stored in the duration field
     * as (damage × 100) to preserve the value during effect serialization and allow recovery after deserialization.</p>
     *
     * @param d the amount to adjust damage by (positive increases, negative decreases)
     */
    public void addDamage(double d) {
        damage = damage + d;

        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;

        // copy damage to duration so we can get it back after serialization
        duration = (int) damage * 100;
    }

    /**
     * Reduce the damage done by this burning effect.
     *
     * <p>Convenience method that calls addDamage() with the negative of the parameter, effectively
     * reducing the current damage by the specified amount. The damage is clamped to [0.5, 10] range
     * after adjustment.</p>
     *
     * @param d the amount to decrease the damage by (will be negated before calling addDamage())
     */
    public void removeDamage(double d) {
        addDamage(d * -1.0);
    }

    /**
     * Get the current damage per tick inflicted by this burning effect.
     *
     * @return the current damage amount (0.5 to 10 health points)
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Perform cleanup when the burning effect is removed.
     *
     * <p>The burning effect has no persistent state to clean up. The fire damage simply ceases
     * when the effect is removed, and no additional cleanup is required.</p>
     */
    @Override
    public void doRemove() {
    }
}
