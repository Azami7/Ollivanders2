package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for spells that apply potion effects to targets.
 *
 * <p>Provides functionality for applying one or more potion effects to living entities within
 * a configurable radius. Effect duration is calculated from the caster's spell experience level.
 * Subclasses can configure effect types, radius, duration range, amplifier, and whether to target
 * the caster or only nearby entities.</p>
 *
 * <p>Configuration:</p>
 *
 * <ul>
 * <li>Duration: calculated from usesModifier * durationModifier, clamped to [minDurationInSeconds, maxDurationInSeconds]</li>
 * <li>Amplifier (strength): set by subclass or calculated via calculateAmplifier() override</li>
 * <li>Effect radius: calculated from usesModifier / 10, clamped to [minEffectRadius, maxEffectRadius]</li>
 * <li>Target filtering: excludes caster unless targetSelf is true; respects affectSingleTarget flag</li>
 * <li>Visual flair: optional particle effect at spell location (controlled by flair flag)</li>
 * </ul>
 *
 * @see <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffect.html">Bukkit PotionEffect documentation</a>
 */
public abstract class AddPotionEffect extends O2Spell {
    /**
     * The duration for this effect.
     */
    int durationInSeconds;

    /**
     * The longest this effect can last.
     */
    int maxDurationInSeconds = 300; // 5 minutes;

    /**
     * The least amount of time this effect can last.
     */
    int minDurationInSeconds = 5; // 5 seconds

    /**
     * Duration modifier is a multiplier on the usesModifier for this spell
     */
    double durationModifier = 1.0; // 100% of usesModifier

    /**
     * Strength modifier, 0 is no modifier. This value can be between -127 and 127.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Effect">https://minecraft.fandom.com/wiki/Effect</a>
     */
    int amplifier = 0;

    /**
     * If true, the spell affects only a single target and stops processing after the first hit.
     * If false, the spell can affect multiple targets up to the spell's limit.
     */
    boolean affectSingleTarget = true;

    /**
     * Whether the spell targets the caster in addition to nearby entities.
     * If true, the caster is the first potential target. If false, only other entities are targeted.
     */
    boolean targetSelf = false;

    /**
     * The calculated radius in blocks around the caster where targets are affected.
     * Calculated from usesModifier / 10, clamped to [minEffectRadius, maxEffectRadius].
     */
    double effectRadius = defaultRadius;

    /**
     * The minimum effect radius in blocks. Used to clamp calculated effectRadius.
     */
    double minEffectRadius = defaultRadius;

    /**
     * The maximum effect radius in blocks. Used to clamp calculated effectRadius.
     */
    double maxEffectRadius = defaultRadius;

    /**
     * If true, spawns a visual particle effect at the spell location when applied.
     */
    boolean flair = false;

    /**
     * The potion effect. Set to luck by default.
     */
    List<PotionEffectType> effectTypes = new ArrayList<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AddPotionEffect(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AddPotionEffect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Apply potion effects to the caster and/or nearby living entities within the effect radius.
     *
     * <p>Called each tick. If the projectile hits a target, the spell is killed before processing effects.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget() || noProjectile)
            kill();

        affectRadius();
    }

    /**
     * Apply potion effects to the caster and/or nearby entities based on configuration.
     *
     * <p>Order of operations:</p>
     *
     * <ul>
     * <li>Apply visual flair if enabled</li>
     * <li>If targetSelf is true, apply effects to the caster and potentially stop</li>
     * <li>Calculate the effect radius based on spell experience</li>
     * <li>Iterate through nearby entities and apply effects, stopping after first target if affectSingleTarget is true</li>
     * </ul>
     */
    void affectRadius() {
        doFlair();

        if (targetSelf) {
            addEffectsToTarget(caster);
            kill(); // because we have hit a target, don't let the spell keep going after this tick

            if (affectSingleTarget)
                return;
        }

        calculateEffectRadius();

        for (LivingEntity livingEntity : getNearbyLivingEntities(effectRadius)) {
            if (livingEntity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            addEffectsToTarget(livingEntity);
            kill(); // because we have hit a target, don't let the spell keep going after this tick

            if (affectSingleTarget)
                return;
        }
    }

    /**
     * Calculate the effect radius based on caster spell experience.
     *
     * <p>Uses the formula: effectRadius = usesModifier / 10, then clamps to [minEffectRadius, maxEffectRadius].</p>
     */
    void calculateEffectRadius() {
        effectRadius = usesModifier / 10;

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Apply visual particle effects at the spell location if enabled.
     */
    void doFlair() {
        if (flair)
            Ollivanders2Common.flair(location, (int) effectRadius, 10);
    }

    /**
     * Add the effect to a target living entity.
     *
     * @param target the target living entity
     */
    void addEffectsToTarget(@NotNull LivingEntity target) {
        // duration
        int durationInTicks = calculateEffectDurationTicks();

        calculateAmplifier();

        for (PotionEffectType effectType : effectTypes) {
            org.bukkit.potion.PotionEffect effect = new org.bukkit.potion.PotionEffect(effectType, durationInTicks, amplifier);
            target.addPotionEffect(effect);

            common.printDebugMessage("Added " + effectType + " to " + target.getName() + " with amplifier of " + amplifier + " and duration of " + durationInSeconds + " seconds", null, null, false);
        }
    }

    /**
     * Calculate the effect duration in ticks based on caster spell experience.
     *
     * <p>Uses the formula: durationInSeconds = usesModifier * durationModifier, then clamps to
     * [minDurationInSeconds, maxDurationInSeconds] and converts to ticks.</p>
     *
     * @return the duration in ticks
     */
    int calculateEffectDurationTicks() {
        durationInSeconds = (int) (usesModifier * durationModifier);
        if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;
        else if (durationInSeconds > maxDurationInSeconds)
            durationInSeconds = maxDurationInSeconds;

        return durationInSeconds * Ollivanders2Common.ticksPerSecond;
    }

    /**
     * Calculate the potion effect amplifier (strength).
     *
     * <p>Default implementation sets a fixed amplifier value. Subclasses can override this method
     * to implement custom scaling based on spell experience or other factors.</p>
     */
    void calculateAmplifier() {
        amplifier = 0; // Night Vision I

        if (usesModifier > (double) (O2Spell.spellMasteryLevel / 2))
            amplifier = 1; // Night Vision II
    }

    /**
     * Get the potion effect types this spell adds.
     *
     * @return a list of the potion effect types
     */
    @NotNull
    public List<PotionEffectType> getEffectTypes() {
        ArrayList<PotionEffectType> types = new ArrayList<>();
        types.addAll(effectTypes);

        return types;
    }

    public int getMinDurationInSeconds() {
        return minDurationInSeconds;
    }

    public int getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public double getMinEffectRadius() {
        return minEffectRadius;
    }

    public double getMaxEffectRadius() {
        return maxEffectRadius;
    }

    public double getEffectRadius() {
        return effectRadius;
    }

    public boolean affectsSingleTarget() {
        return affectSingleTarget;
    }

    public boolean targetsSelf() {
        return targetSelf;
    }

    public int getAmplifier() {
        return amplifier;
    }
}
