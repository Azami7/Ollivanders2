package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Damages the target with periodic fire damage plus smoke and burning sound effects. Damage never reduces the target
 * below 1 health, so it cannot kill. Detectable via Informous and Legilimens.
 *
 * @author Azami7
 */
public class BURNING extends O2Effect {
    /**
     * Lower clamp for {@link #damage}, in health points.
     */
    private final double minDamage = 0.5;

    /**
     * Upper clamp for {@link #damage}, in health points.
     */
    private final double maxDamage = 10;

    /**
     * The damage in health points applied on each damage tick. Adjusted via {@link #addDamage(double)} and clamped to
     * [{@link #minDamage}, {@link #maxDamage}].
     */
    private double damage;

    /**
     * Interval between damage applications, in seconds.
     */
    private int damageFrequencyInSeconds = 3;

    /**
     * Ticks remaining until the next damage application; reset to {@link #damageFrequencyInSeconds} × ticksPerSecond
     * after each hit.
     */
    private int damageCooldown = 0;

    /**
     * Constructor
     * <p>
     * Initial damage is derived from the duration as {@code duration / 100}; the initial value is not clamped.
     * </p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration in game ticks; also sets the initial per-tick damage as duration / 100
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

    @Override
    public void checkEffect() {
        age(1);

        if (damageCooldown <= 0) {
            doDamage();
            damageCooldown = damageFrequencyInSeconds * Ollivanders2Common.ticksPerSecond;
        }
        else
            damageCooldown = damageCooldown - 1;
    }

    /**
     * Damage the target by the current {@link #damage}, then play the smoke particle and fire-hurt sound. Damage is
     * skipped if it would drop the target to 1 health or below, so the effect cannot kill.
     */
    private void doDamage() {
        if ((target.getHealth() - damage) > 1)
            target.damage(damage);

        target.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 4);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, 0);
    }

    /**
     * Adjust the per-tick damage by the given amount, clamped to [{@link #minDamage}, {@link #maxDamage}]. The result
     * is mirrored into the duration field (as damage × 100) so the value survives serialization.
     *
     * @param d the signed amount to add to the current damage
     */
    public void addDamage(double d) {
        damage = damage + d;

        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;

        // copy damage to duration so we can get it back after serialization
        duration = (int)(damage * 100);
    }

    /**
     * Reduce the per-tick damage by the given amount, clamped to [{@link #minDamage}, {@link #maxDamage}].
     *
     * @param d the amount to subtract from the current damage
     */
    public void removeDamage(double d) {
        addDamage(d * -1.0);
    }

    /**
     * Get the current per-tick damage in health points.
     *
     * @return the current damage, within [{@link #minDamage}, {@link #maxDamage}]
     */
    public double getDamage() {
        return damage;
    }

    @Override
    public void doRemove() {
    }
}
