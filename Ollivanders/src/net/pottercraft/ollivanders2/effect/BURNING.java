package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Add burning damage effect to a player.
 */
public class BURNING extends O2Effect {
    /**
     * The minimum damage the effect will do
     */
    private final double minDamage = 0.5;

    /**
     * The maximum damage the effect can do
     */
    private final double maxDamage = 10;

    /**
     * The damage this instance of the effect will do
     */
    private double damage = 0.0;

    /**
     * How often does the effect cause damage to the player
     */
    private int damageFrequencyInSeconds = 3;

    /**
     * Tick counter for the cooldown on damage
     */
    private int damageCooldown = 0;

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the player this effect acts on
     */
    public BURNING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.BURNING;
        informousText = legilimensText = "is afflicted with a terrible burning";

        divinationText.add("shall be cursed");
        divinationText.add("will be consumed by fire");
        divinationText.add("will burn from within");

        damage = (double) duration / 100;
    }

    /**
     * Age the effect if not permanent and damage the player if this is the right tick
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
     * Damage the player but do not kill them
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
     * Increase the damage done by this burning effect
     *
     * @param d the amount to increase the damage by
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
     * Decrease the damage done by this effect
     *
     * @param d the amount to decrease the damage by
     */
    public void removeDamage(double d) {
        addDamage(d * -1.0);
    }

    /**
     * Get the amount of damage this effect does
     *
     * @return the damage done by this effect
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Override default set permanent to make sure it doesnt get called and alter the duration since this is
     * already a permanent effect.
     *
     * @param perm true if this is permanent, false otherwise
     */
    @Override
    public void setPermanent(boolean perm) {
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}
